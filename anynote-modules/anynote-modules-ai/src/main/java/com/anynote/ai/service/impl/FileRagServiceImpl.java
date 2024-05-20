package com.anynote.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.anynote.ai.api.exception.RagLimitException;
import com.anynote.ai.api.model.bo.RagFileIndexReq;
import com.anynote.ai.api.model.bo.RagFileIndexRes;
import com.anynote.ai.api.model.bo.RagFileQueryReq;
import com.anynote.ai.api.model.bo.RagFileQueryRes;
import com.anynote.ai.api.model.po.ChatConversation;
import com.anynote.ai.api.model.po.ChatMessage;
import com.anynote.ai.api.model.po.RagLog;
import com.anynote.ai.datascope.annotation.RequiresChatConversationPermissions;
import com.anynote.ai.enums.ChatConversationPermissions;
import com.anynote.ai.enums.ChatRole;
import com.anynote.ai.enums.ChatType;
import com.anynote.ai.model.bo.DocRagQueryParam;
import com.anynote.ai.model.vo.DocQueryVO;
import com.anynote.ai.service.ChatService;
import com.anynote.ai.service.FileRagService;
import com.anynote.common.redis.service.ConfigService;
import com.anynote.common.rocketmq.callback.RocketmqSendCallbackBuilder;
import com.anynote.common.rocketmq.properties.RocketMQProperties;
import com.anynote.common.rocketmq.tags.RagTagsEnum;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.*;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.note.api.RemoteDocService;
import com.anynote.note.api.model.vo.DocVO;
import com.anynote.system.api.model.bo.LoginUser;
import com.google.gson.Gson;

import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author 称霸幼儿园
 */
@Service
@Slf4j
public class FileRagServiceImpl implements FileRagService {

//    @Resource
//    private RedisService redisService;

    @Resource
    private ConfigService configService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private WebClient webClient;

    @Resource
    private ChatService chatService;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private RemoteDocService remoteDocService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RocketMQProperties rocketMQProperties;

    private final String CONTENT_TYPE = "text/plain;charset=UTF-8";

    @Override
    public RagFileIndexRes indexFile(RagFileIndexReq ragFileIndexReq) {
        String aiServerAddress = configService.getAIServerAddress();
        HttpEntity<RagFileIndexReq> httpEntity = new HttpEntity<>(ragFileIndexReq);
        ParameterizedTypeReference<ResData<RagFileIndexRes>> responseType =
                new ParameterizedTypeReference<ResData<RagFileIndexRes>>() {};
        ResponseEntity<ResData<RagFileIndexRes>> response = restTemplate.exchange(aiServerAddress + "/api/rag/index",
                HttpMethod.POST, httpEntity, responseType);

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new BusinessException("调用文件索引服务器失败");
        }

        return RemoteResDataUtil.getResData(response.getBody(), "建立索引失败");
    }


    @Override
    public void queryFile(RagFileQueryReq ragFileQueryReq) throws IOException {
        String aiServerAddress = configService.getAIServerAddress();
//        HttpEntity<RagFileQueryReq> httpEntity = new HttpEntity<>(ragFileQueryReq);
        ParameterizedTypeReference<ResData<RagFileQueryRes>> responseType =
                new ParameterizedTypeReference<ResData<RagFileQueryRes>>() {};

        Flux<RagFileQueryRes> resFlux = webClient.post()
                .uri(aiServerAddress + "/api/rag/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(JSON.toJSONString(ragFileQueryReq)))
                .exchangeToFlux(res -> {
                    if (res.statusCode().isError()) {
                        throw new BusinessException("Rag查询失败");
                    }
                    else {
                        return res.bodyToFlux(RagFileQueryRes.class);
                    }
                });
        HttpServletResponse response = ServletUtils.getResponse();
        response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        Writer writer = response.getWriter();

        CountDownLatch latch = new CountDownLatch(1);

        resFlux.subscribe(
                value -> {
                    String resJson = JSON.toJSONString(value);
                    log.info(resJson);
                    try {
                        writer.write(String.format("id: %s\nevent: message\ndata: %s\n\n", System.currentTimeMillis(), resJson));
                        writer.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    error.printStackTrace();
                    latch.countDown();
                },
                latch::countDown
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


//        ResponseEntity<ResData<RagFileQueryRes>> response = restTemplate.exchange(aiServerAddress + "/api/rag/query",
//                HttpMethod.POST, httpEntity, responseType);
//        if (!HttpStatus.OK.equals(response.getStatusCode())) {
//            throw new BusinessException("调用AI服务器失败");
//        }
//        return RemoteResDataUtil.getResData(response.getBody(), "查询失败");
    }


    private final String RAG_LIMIT_LUA_SCRIPT = "local userId = ARGV[1]\n" +
            "local date = ARGV[2]\n" +
            "local ragCountKey = 'RAG:COUNT:' .. string.sub(date, 2, -2) .. ':' .. userId\n" +
            "local maxCount = ARGV[3]\n" +
            "local isExists = redis.call('EXISTS', ragCountKey)\n" +
            "local currentCount = 0\n" +
            "if isExists == 1 then\n" +
            "    currentCount = tonumber(redis.call('get', ragCountKey))\n" +
            "else\n" +
            "    redis.call('SET', ragCountKey, 0)\n" +
            "end\n" +
            "if(currentCount >= tonumber(ARGV[3])) then\n" +
            "    return '1'\n" +
            "end\n" +
            "redis.call('incr', ragCountKey)\n" +
            "return '0'";

    private void docQueryRagLimitCheck(Long userId) {
//        LoginUser loginUser = tokenUtil.getLoginUser();
        DefaultRedisScript<String> script = new DefaultRedisScript<>(RAG_LIMIT_LUA_SCRIPT);
        script.setResultType(String.class);
        Integer res = (Integer) redisTemplate.execute(script, Collections.emptyList(), userId, DateUtils.getCurrentDateString(),
                configService.getRagMaxDayCount());
        if (StringUtils.isNotNull(res) && res == 0) {
            return;
        }
        throw new RagLimitException();
    }


    @RequiresChatConversationPermissions(ChatConversationPermissions.EDIT)
    @Override
    public void queryDoc(DocRagQueryParam docRagQueryParam) throws IOException {
        Gson gson = new Gson();
        Date date = new Date();
        LoginUser loginUser = tokenUtil.getLoginUser();
        Long conversationId = null;
        if (StringUtils.isNull(docRagQueryParam.getConversationId())) {
            conversationId = chatService.insertChatConversation(ChatConversation.builder()
                    .docId(docRagQueryParam.getDocId())
                    .title(docRagQueryParam.getPrompt().length() > 10 ? docRagQueryParam.getPrompt().substring(0, 10) + "..." : docRagQueryParam.getPrompt())
                    .type(0)
                    .permissions("70000")
                    .deleted(0)
                    .createBy(loginUser.getUserId())
                    .createTime(date)
                    .updateBy(loginUser.getUserId())
                    .updateTime(date)
                    .build());
        }
        else {
            conversationId = docRagQueryParam.getConversationId();
        }

        // 提问消息保存
        List<ChatMessage> chatMessageList = chatService.selectChatMessageList(conversationId);
        ChatMessage askMessage = ChatMessage.builder()
                .content(docRagQueryParam.getPrompt())
                .conversationId(conversationId)
                .orderIndex(0)
                .role(ChatRole.USER.getValue())
                .type(ChatType.DOC_RAG.getValue())
                .docId(docRagQueryParam.getDocId())
                .deleted(0)
                .createBy(loginUser.getUserId())
                .createTime(date)
                .updateBy(loginUser.getUserId())
                .updateTime(date)
                .build();
        if (!chatMessageList.isEmpty()) {
            askMessage.setOrderIndex(chatMessageList.get(chatMessageList.size()-1).getOrderIndex() + 1);
        }
        this.chatService.insertChatMessage(askMessage);

        // RAG次数限制查询
        this.docQueryRagLimitCheck(loginUser.getUserId());

        // 获取DOC信息
        DocVO docVO = RemoteResDataUtil.getResData(remoteDocService.getDoc(docRagQueryParam.getDocId()),
                "获取DOC失败");

        //创建日志
        RagLog ragLog = RagLog.builder()
                .fileHash(docVO.getHash())
                .fileName(docVO.getDocName())
                .author("UNKNOWN")
                .category("UNKNOWN")
                .description("")
                .prompt(docRagQueryParam.getPrompt())
                .startTime(date)
                .deleted(0)
                .createTime(date)
                .updateTime(date)
                .updateBy(loginUser.getUserId())
                .createBy(loginUser.getUserId())
                .build();

        // 构建请求
        RagFileQueryReq req = RagFileQueryReq.builder()
                .file_hash(docVO.getHash())
                .prompt(docRagQueryParam.getPrompt())
                .file_name(docVO.getDocName())
                .author(docVO.getCreatorNickname())
                .category("UNKNOWN")
                .description("no description")
                .build();

        // 发送请求
        ServletUtils.setStreamResponseHeader();
        Writer writer = ServletUtils.getWriter();
        String aiServerAddress = configService.getAIServerAddress();
        Flux<RagFileQueryRes> resFlux = webClient.post()
                .uri(aiServerAddress + "/api/rag/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(gson.toJson(req)))
                .exchangeToFlux(res -> {
                    if (res.statusCode().isError()) {
                        throw new BusinessException("Rag查询失败");
                    }
                    else {
                        return res.bodyToFlux(RagFileQueryRes.class);
                    }
                });

        CountDownLatch latch = new CountDownLatch(1);
        RagFileQueryRes[] ragFileQueryRes = {null};
        resFlux.subscribe(
                value -> {
                    String resJson = null;
                    if (value.getStatus().equals("failed")) {
                        value.setMessage("生成失败，请重试");
                        resJson = gson.toJson(ResUtil.error(DocQueryVO.builder()
                                .status(value.getStatus())
                                .message(value.getResult())
                                .build()));
                    }
                    else {
                        resJson = gson.toJson(ResUtil.success(DocQueryVO.builder()
                                .status(value.getStatus())
                                .message(value.getResult())
                                .build()));
                    }
                    ragFileQueryRes[0] = value;
                    log.info(resJson);
                    ServletUtils.sendStreamResponse(writer, resJson);
                },
                error -> {
                    error.printStackTrace();
                    latch.countDown();
                },
                () -> {
                    Date finishDate = new Date();
                    ChatMessage resMessage = ChatMessage.builder()
                            .conversationId(askMessage.getConversationId())
                            .orderIndex(askMessage.getOrderIndex() + 1)
                            .content(ragFileQueryRes[0].getResult())
                            .role(ChatRole.BOT.getValue())
                            .type(ChatType.DOC_RAG.getValue())
                            .docId(docVO.getId())
                            .deleted(0)
                            .createBy(loginUser.getUserId())
                            .createTime(finishDate)
                            .updateBy(loginUser.getUserId())
                            .updateTime(finishDate)
                            .build();
                    chatService.insertChatMessage(resMessage);
                    if (ragFileQueryRes[0].getStatus().equals("finished")) {
                        ragLog.setResult(0);
                    }
                    else {
                        ragLog.setResult(1);
                    }
                    log.info(gson.toJson(ragLog));
                    log.info(gson.toJson(ragFileQueryRes[0]));
                    ragLog.setMessage(ragFileQueryRes[0].getResult());
                    ragLog.setEndTime(finishDate);
                    String destination = rocketMQProperties.getRagTopic() + ":" + RagTagsEnum.SAVE_RAG_LOG.name();
                    rocketMQTemplate.asyncSend(destination, gson.toJson(ragLog),
                            RocketmqSendCallbackBuilder.commonCallback());
                    latch.countDown();
                }
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }


    @RequiresChatConversationPermissions(ChatConversationPermissions.EDIT)
    @Override
    public Flux<ResData<DocQueryVO>> queryDocV2(DocRagQueryParam docRagQueryParam) throws IOException {
        Gson gson = new Gson();
        Date date = new Date();
        LoginUser loginUser = tokenUtil.getLoginUser();
        Long conversationId = null;
        if (StringUtils.isNull(docRagQueryParam.getConversationId())) {
            conversationId = chatService.insertChatConversation(ChatConversation.builder()
                    .docId(docRagQueryParam.getDocId())
                    .title(docRagQueryParam.getPrompt().length() > 10 ? docRagQueryParam.getPrompt().substring(0, 10) + "..." : docRagQueryParam.getPrompt())
                    .type(0)
                    .permissions("70000")
                    .deleted(0)
                    .createBy(loginUser.getUserId())
                    .createTime(date)
                    .updateBy(loginUser.getUserId())
                    .updateTime(date)
                    .build());
        }
        else {
            conversationId = docRagQueryParam.getConversationId();
        }

        // 提问消息保存
        List<ChatMessage> chatMessageList = chatService.selectChatMessageList(conversationId);
        ChatMessage askMessage = ChatMessage.builder()
                .content(docRagQueryParam.getPrompt())
                .conversationId(conversationId)
                .orderIndex(0)
                .role(ChatRole.USER.getValue())
                .type(ChatType.DOC_RAG.getValue())
                .docId(docRagQueryParam.getDocId())
                .deleted(0)
                .createBy(loginUser.getUserId())
                .createTime(date)
                .updateBy(loginUser.getUserId())
                .updateTime(date)
                .build();
        if (!chatMessageList.isEmpty()) {
            askMessage.setOrderIndex(chatMessageList.get(chatMessageList.size()-1).getOrderIndex() + 1);
        }
        this.chatService.insertChatMessage(askMessage);


        // 获取DOC信息
        DocVO docVO = RemoteResDataUtil.getResData(remoteDocService.getDoc(docRagQueryParam.getDocId()),
                "获取DOC失败");

        //创建日志
        RagLog ragLog = RagLog.builder()
                .fileHash(docVO.getHash())
                .fileName(docVO.getDocName())
                .author("UNKNOWN")
                .category("UNKNOWN")
                .description("")
                .prompt(docRagQueryParam.getPrompt())
                .startTime(date)
                .deleted(0)
                .createTime(date)
                .updateTime(date)
                .updateBy(loginUser.getUserId())
                .createBy(loginUser.getUserId())
                .build();


        // RAG次数限制查询
        try {
            this.docQueryRagLimitCheck(loginUser.getUserId());
        } catch (RagLimitException e) {
            log.error(e.getMessage());

            Date finishDate = new Date();
            ragLog.setResult(1);
            ragLog.setMessage("您今天的RAG使用次数已用完");
            ragLog.setEndTime(finishDate);
            String destination = rocketMQProperties.getRagTopic() + ":" + RagTagsEnum.SAVE_RAG_LOG.name();
            rocketMQTemplate.asyncSend(destination, gson.toJson(ragLog),
                    RocketmqSendCallbackBuilder.commonCallback());

            ChatMessage resMessage = ChatMessage.builder()
                    .conversationId(askMessage.getConversationId())
                    .orderIndex(askMessage.getOrderIndex() + 1)
                    .content("您今天的RAG使用次数已用完")
                    .role(ChatRole.BOT.getValue())
                    .type(ChatType.DOC_RAG.getValue())
                    .docId(docVO.getId())
                    .deleted(0)
                    .createBy(loginUser.getUserId())
                    .createTime(finishDate)
                    .updateBy(loginUser.getUserId())
                    .updateTime(finishDate)
                    .build();
            chatService.insertChatMessage(resMessage);

            return Flux.just(ResUtil.error(DocQueryVO.builder()
                    .status("failed")
                    .message("您今天的RAG使用次数已用完")
                    .conversationId(conversationId).build()));
        }

        // 构建请求
        RagFileQueryReq req = RagFileQueryReq.builder()
                .file_hash(docVO.getHash())
                .prompt(docRagQueryParam.getPrompt())
                .file_name(docVO.getDocName())
                .author(docVO.getCreatorNickname())
                .category("UNKNOWN")
                .description("no description")
                .build();

        ParameterizedTypeReference<ResData<RagFileQueryRes>> resType =
                new ParameterizedTypeReference<ResData<RagFileQueryRes>>() {};



        RagFileQueryRes[] ragFileQueryRes = {null};
        String aiServerAddress = configService.getAIServerAddress();
        Long finalConversationId = conversationId;
        return webClient.post()
                .uri(aiServerAddress + "/api/rag/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(gson.toJson(req)))
                .retrieve()
                .bodyToFlux(RagFileQueryRes.class)
                .map(value -> {
                    String resJson = null;
                    ragFileQueryRes[0] = value;
                    log.info(gson.toJson(value));
                    if (value.getStatus().equals("failed")) {
                        value.setMessage("生成失败，请重试");
                        return ResUtil.error(DocQueryVO.builder()
                                .status(value.getStatus())
                                .message(value.getResult())
                                .conversationId(finalConversationId)
                                .build());
                    }
                    else {
                        return ResUtil.success(DocQueryVO.builder()
                                .status(value.getStatus())
                                .message(value.getResult())
                                .conversationId(finalConversationId)
                                .build());
                    }
                })
                .doFinally(signalType -> {
                    Date finishDate = new Date();
                    ChatMessage resMessage = ChatMessage.builder()
                            .conversationId(askMessage.getConversationId())
                            .orderIndex(askMessage.getOrderIndex() + 1)
                            .content(ragFileQueryRes[0] != null ? ragFileQueryRes[0].getResult() : "出现异常请稍后重试")
                            .role(ChatRole.BOT.getValue())
                            .type(ChatType.DOC_RAG.getValue())
                            .docId(docVO.getId())
                            .deleted(0)
                            .createBy(loginUser.getUserId())
                            .createTime(finishDate)
                            .updateBy(loginUser.getUserId())
                            .updateTime(finishDate)
                            .build();
                    chatService.insertChatMessage(resMessage);
                    if (ragFileQueryRes[0].getStatus().equals("finished")) {
                        ragLog.setResult(0);
                    }
                    else {
                        ragLog.setResult(1);
                    }
                    log.info(gson.toJson(ragLog));
                    log.info(gson.toJson(ragFileQueryRes[0]));
                    ragLog.setMessage(ragFileQueryRes[0].getResult());
                    ragLog.setEndTime(finishDate);
                    String destination = rocketMQProperties.getRagTopic() + ":" + RagTagsEnum.SAVE_RAG_LOG.name();
                    rocketMQTemplate.asyncSend(destination, gson.toJson(ragLog),
                            RocketmqSendCallbackBuilder.commonCallback());
                }).doOnError(e -> {
                    log.error(e.getMessage());
                }).onErrorReturn(ResUtil.error(DocQueryVO.builder()
                        .status("failed")
                        .message("出现异常请稍后重试")
                        .conversationId(finalConversationId)
                        .build()));
    }

    private Flux<ResData<DocQueryVO>> query(RagFileQueryReq req, Function<RagFileQueryRes, ResData<DocQueryVO>> map,
                                            Consumer<SignalType> doFinally, ResData<DocQueryVO> onErrorReturn) {
        Gson gson = new Gson();
        String aiServerAddress = configService.getAIServerAddress();
        return webClient.post()
                .uri(aiServerAddress + "/api/rag/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(gson.toJson(req)))
                .retrieve()
                .bodyToFlux(RagFileQueryRes.class)
                .map(map)
                .doFinally(doFinally)
                .doOnError(e -> {
                    log.error(e.getMessage());
                })
                .onErrorReturn(onErrorReturn);
    }


    @Override
    public Flux<ResData<DocQueryVO>> queryDocFree(DocRagQueryParam docRagQueryParam) throws IOException {
        Gson gson = new Gson();
        Date date = new Date();

        DocVO docVO = RemoteResDataUtil.getResData(remoteDocService.getPublicDoc(docRagQueryParam.getDocId()),
                "获取DOC失败");

        //创建日志
        RagLog ragLog = RagLog.builder()
                .fileHash(docVO.getHash())
                .fileName(docVO.getDocName())
                .author("UNKNOWN")
                .category("UNKNOWN")
                .description("")
                .prompt(docRagQueryParam.getPrompt())
                .startTime(date)
                .deleted(0)
                .createTime(date)
                .updateTime(date)
                .updateBy(0L)
                .createBy(0L)
                .build();

        // 构建请求
        RagFileQueryReq req = RagFileQueryReq.builder()
                .file_hash(docVO.getHash())
                .prompt(docRagQueryParam.getPrompt())
                .file_name(docVO.getDocName())
                .author(docVO.getCreatorNickname())
                .category("UNKNOWN")
                .description("no description")
                .build();

        RagFileQueryRes[] ragFileQueryRes = {null};

        return this.query(req, value -> {
            ragFileQueryRes[0] = value;
            log.info(gson.toJson(value));
            if (value.getStatus().equals("failed")) {
                value.setMessage("生成失败，请重试");
                return ResUtil.error(DocQueryVO.builder()
                        .status(value.getStatus())
                        .message(value.getResult())
                        .conversationId(null)
                        .build());
            }
            else {
                return ResUtil.success(DocQueryVO.builder()
                        .status(value.getStatus())
                        .message(value.getResult())
                        .conversationId(null)
                        .build());
            }
        }, signalType -> {
            Date finishDate = new Date();
            if (StringUtils.isNotNull(ragFileQueryRes[0]) && ragFileQueryRes[0].getStatus().equals("finished")) {
                ragLog.setResult(0);
            }
            else {
                ragLog.setResult(1);
            }
            log.info(gson.toJson(ragLog));
            ragLog.setMessage(StringUtils.isNotNull(ragFileQueryRes[0]) ? ragFileQueryRes[0].getResult() : "出现异常请稍后重试");
            ragLog.setEndTime(finishDate);
            String destination = rocketMQProperties.getRagTopic() + ":" + RagTagsEnum.SAVE_RAG_LOG.name();
            rocketMQTemplate.asyncSend(destination, gson.toJson(ragLog),
                    RocketmqSendCallbackBuilder.commonCallback());
        }, ResUtil.error(DocQueryVO.builder()
                .status("failed")
                .message("出现异常请稍后重试")
                .conversationId(null)
                .build()));
    }
}

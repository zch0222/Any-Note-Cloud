package com.anynote.ai.nio.datascope.aspect;


import com.anynote.ai.api.RemoteChatConversationService;
import com.anynote.ai.api.enums.ChatConversationPermissions;
import com.anynote.ai.api.model.bo.ChatConversationQueryParam;
import com.anynote.ai.nio.datascope.annotation.RequiresChatConversationPermissions;
import com.anynote.ai.nio.service.ChatService;
//import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.StringUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;

@Aspect
@Component
@Order(1)
@Slf4j
public class RequiresChatConversationPermissionsAspect {

//    @Resource
//    private TokenUtil tokenUtil;


    @Resource
    private RemoteChatConversationService remoteChatConversationService;

    @Resource
    private ChatService chatService;

    @Resource
    private WebClient webClient;

    public final static String CHAT_CONVERSATION_PERMISSIONS = "chatConversationPermissions";


//    @Around("@annotation(requiresChatConversationPermissions)")
//    private Object doAround(ProceedingJoinPoint joinPoint,
//                            RequiresChatConversationPermissions requiresChatConversationPermissions) {
//        ChatConversationQueryParam queryParam = getParam(joinPoint);
//        if (StringUtils.isNull(queryParam)) {
//            throw new BusinessException("未知异常，请联系管理员");
//        }
//        Mono.fromCallable(() -> authPermissions(queryParam, requiresChatConversationPermissions.value()))
//                .flatMap(value -> {
//                    if (value) {
//                        try {
//                            return (Mono)joinPoint.proceed();
//                        } catch (Throwable e) {
//                            return Mono.error(e);
//                        }
//                    }
//                    return Mono.error(new AuthException("没有权限执行操作"));
//                })
//                .publishOn(Schedulers.boundedElastic()).subscribe();
//    }

    @Before("@annotation(requiresChatConversationPermissions)")
    public void doBefore(JoinPoint joinPoint, RequiresChatConversationPermissions requiresChatConversationPermissions) {
        ChatConversationQueryParam queryParam = getParam(joinPoint);
        if (StringUtils.isNull(queryParam)) {
            throw new BusinessException("未知异常，请联系管理员");
        }
        authPermissions(queryParam, requiresChatConversationPermissions.value());

    }



    private void authPermissions(ChatConversationQueryParam queryParam, ChatConversationPermissions reqPermissions) {
        if (StringUtils.isNull(queryParam.getConversationId())) {
            return;
        }
        log.info(queryParam.getAccessToken());

        ChatConversationPermissions permissions = RemoteResDataUtil.getResData(remoteChatConversationService
                .getChatConversationPermissions(queryParam.getConversationId(), "inner", queryParam.getAccessToken()), "ee");
//        ParameterizedTypeReference<ResData<ChatConversationPermissions>> responseType =
//                new ParameterizedTypeReference<ResData<ChatConversationPermissions>>() {};
//        log.info("START WEBCLIENT!");
        Mono.fromCallable(() -> remoteChatConversationService
                .getChatConversationPermissions(queryParam.getConversationId(),
                        "inner", queryParam.getAccessToken()))
                .publishOn(Schedulers.boundedElastic()).log()
                .flatMap(value -> {
                    log.info("FLAT MAP");
                    return Mono.just(value);
                })
                .subscribeOn(Schedulers.single())
                .subscribe(value -> {
                    log.info(new Gson().toJson(value));
                });
//        webClient.get().uri("http://" + ServiceNameConstants.AI_SERVICE + "/chat/conversations/"
//                + queryParam.getConversationId() + "/permissions")
//                .header("accessToken", queryParam.getAccessToken())
//                .header("from-source", "inner")
//                .retrieve().bodyToFlux(responseType).flatMap(value -> {
//                    log.info("RES---------");
//                    return Mono.just(value);
//                }).subscribe(value -> log.info(new Gson().toJson(value)));
//        if (permissions.getValue() < reqPermissions.getValue()) {
//            throw new AuthException("没有权限执行操作");
//        }
    }


    private ChatConversationQueryParam getParam(JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNull(params) || !(params instanceof ChatConversationQueryParam)) {
            return null;
        }
        return (ChatConversationQueryParam) params;
    }

}

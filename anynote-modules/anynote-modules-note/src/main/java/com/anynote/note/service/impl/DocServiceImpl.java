package com.anynote.note.service.impl;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.anynote.ai.api.RagService;
import com.anynote.ai.api.RemoteRagService;
import com.anynote.ai.api.RemoteTranslateService;
import com.anynote.ai.api.exception.RagLimitException;
import com.anynote.ai.api.model.bo.RagFileQueryReq;
import com.anynote.common.datascope.model.bo.QueryParam;
import com.anynote.common.redis.service.ConfigService;
import com.anynote.common.rocketmq.callback.RocketmqSendCallbackBuilder;
import com.anynote.common.rocketmq.properties.RocketMQProperties;
import com.anynote.common.rocketmq.tags.DocTagsEnum;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.constant.FileConstants;
import com.anynote.core.constant.HuaweiOBSConstants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.auth.TokenException;
import com.anynote.core.exception.user.UserParamException;
import com.anynote.core.utils.*;
import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.RemoteFileService;
import com.anynote.file.api.enums.FileSources;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.file.api.model.dto.CompleteUploadDTO;
import com.anynote.file.api.model.dto.CreateHuaweiOBSTemporarySignatureDTO;
import com.anynote.file.api.model.po.FilePO;
import com.anynote.note.api.model.po.Doc;
import com.anynote.note.datascope.annotation.KnowledgeBaseDataScope;
import com.anynote.note.datascope.annotation.RequiresDocPermissions;
import com.anynote.note.datascope.annotation.RequiresKnowledgeBasePermissions;
import com.anynote.note.datascope.aspect.RequiresDocPermissionsAspect;
import com.anynote.note.enums.DocIndexStatus;
import com.anynote.note.enums.DocPermissions;
import com.anynote.note.enums.DocType;
import com.anynote.note.enums.KnowledgeBasePermissions;
import com.anynote.note.mapper.DocMapper;
import com.anynote.note.model.bo.*;
import com.anynote.note.model.vo.DocListVO;
import com.anynote.note.model.vo.DocQueryVO;
import com.anynote.note.api.model.vo.DocVO;
import com.anynote.note.service.DocService;
import com.anynote.note.service.KnowledgeBaseService;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.po.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

/**
 * 文档服务器 IMPL
 * @author 称霸幼儿园
 */
@Service
@Slf4j
public class DocServiceImpl extends ServiceImpl<DocMapper, Doc>
        implements DocService {

    @Resource
    private RemoteFileService remoteFileService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RocketMQProperties rocketMQProperties;

    @Resource
    private KnowledgeBaseService knowledgeBaseService;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private RemoteRagService remoteRagService;

    @Resource
    private ConfigService configService;

    @Resource
    private WebClient webClient;

    @Resource
    private RemoteTranslateService remoteTranslateService;

    @Resource
    private RagService ragService;


    @Override
    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "您没有权限上传PDF文档")
    public CreateResEntity createPDF(PDFCreateParam pdfCreateParam) {
        Date date = new Date();
        LoginUser loginUser = tokenUtil.getLoginUser();
        ResData<FilePO> resData = remoteFileService.uploadFile(pdfCreateParam.getPdf(),
                FileConstants.DOC_PDF, loginUser.getSysUser().getId(), pdfCreateParam.getUploadId(), FileSources.KNOWLEDGE_BASE_DOC.getValue());

        FilePO filePO = RemoteResDataUtil.getResData(resData, "上传文档失败");
        Doc doc = Doc.builder()
                .fileId(filePO.getId())
                .name(filePO.getOriginalFileName())
                .knowledgeBaseId(pdfCreateParam.getKnowledgeBaseId())
                .type(DocType.PDF.getValue())
                .dataScope(1)
                .permissions("70000")
                .deleted(0)
                .createBy(loginUser.getSysUser().getId())
                .createTime(date)
                .updateBy(loginUser.getSysUser().getId())
                .updateTime(date)
                .build();
        this.baseMapper.insert(doc);

        return CreateResEntity.builder()
                .id(doc.getId())
                .build();
    }


    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "没有权限进行上传")
    @Override
    public HuaweiOBSTemporarySignature createDocUploadTempLink(DocUploadSignatureCreateParam docUploadSignatureCreateParam) {
        return RemoteResDataUtil.getResData(remoteFileService.createHuaweiOBSTemporarySignature(
                CreateHuaweiOBSTemporarySignatureDTO.builder()
                        .contentType(docUploadSignatureCreateParam.getContentType())
                        .expireSeconds(HuaweiOBSConstants.DOC_TEMPORARY_SIGNATURE_EXPIRE_SECONDS)
                        .fileName(docUploadSignatureCreateParam.getDocName())
                        .path(FileConstants.DOC_PATH_TEMPLATE)
                        .source(FileSources.KNOWLEDGE_BASE_DOC.getValue())
                        .build()), "上传文档失败");
    }

    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "没有权限进行上传")
    @Override
    public CreateResEntity completeDocUpload(DocCreateParam docCreateParam) {
        FilePO filePO = RemoteResDataUtil.getResData(remoteFileService.completeHuaweiOBSUpload(CompleteUploadDTO
                .builder()
                .uploadId(docCreateParam.getUploadId())
                .hash(docCreateParam.getHash())
                .build()), "上传文档失败");
        Date date = new Date();
        LoginUser loginUser = tokenUtil.getLoginUser();
        Doc doc = Doc.builder()
                .fileId(filePO.getId())
                .name(filePO.getOriginalFileName())
                .knowledgeBaseId(docCreateParam.getKnowledgeBaseId())
                .type(DocType.PDF.getValue())
                .indexStatus(DocIndexStatus.NOT_INDEXED.getValue())
                .dataScope(3)
                .permissions("77400")
                .deleted(0)
                .createBy(loginUser.getUserId())
                .createTime(date)
                .updateBy(loginUser.getUserId())
                .updateTime(date)
                .build();
        this.baseMapper.insert(doc);

        // 异步建立索引
        String destination = rocketMQProperties.getDocTopic() + ":" + DocTagsEnum.RAG_INDEX.name();
        rocketMQTemplate.asyncSend(destination, doc.getId(), RocketmqSendCallbackBuilder.commonCallback());

        String translateDocNameDestination = rocketMQProperties.getDocTopic() + ":" +
                DocTagsEnum.TRANSLATE_DOC_NAME_TO_ENGLISH.name();
        rocketMQTemplate.asyncSend(translateDocNameDestination, doc.getId(), RocketmqSendCallbackBuilder.commonCallback());

        return CreateResEntity.builder()
                .id(doc.getId())
                .build();
    }

    @Override
    @KnowledgeBaseDataScope("n_doc")
    public PageBean<DocListVO> getDocList(DocQueryParam queryParam) {
        PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize(), "update_time DESC");
        List<DocListVO> docListVOList = this.baseMapper.selectDocList(queryParam);
        PageInfo<DocListVO> pageInfo = new PageInfo<>(docListVOList);
        return PageBean.<DocListVO>builder()
                .current(queryParam.getPage())
                .pages(pageInfo.getPages())
                .rows(docListVOList)
                .total(pageInfo.getTotal())
                .build();
    }

    @Override
    public DocPermissions getDocPermissions(Long docId) {
        LoginUser loginUser = null;
        try {
            loginUser = tokenUtil.getLoginUser();
        } catch (TokenException e) {
            log.error(e.getErrorMessage());
            log.info("匿名访问文档，ID:" + docId);
        }

        LambdaQueryWrapper<Doc> docLambdaQueryWrapper = new LambdaQueryWrapper<>();
        docLambdaQueryWrapper
                .eq(Doc::getId, docId)
                .select(Doc::getPermissions, Doc::getCreateBy, Doc::getKnowledgeBaseId, Doc::getId);
        Doc doc = this.baseMapper.selectOne(docLambdaQueryWrapper);

        if (StringUtils.isNull(doc)) {
            throw new UserParamException("文档不存在");
        }

        if (loginUser == null) {
            return DocPermissions.parse(Integer.parseInt(doc.getPermissions().substring(4, 5)));
        }

        if (SysUser.isAdminX(loginUser.getSysUser().getRole())) {
            return DocPermissions.MANAGE;
        }


        int permission = 0;
        if (loginUser.getUserId().equals(doc.getCreateBy())) {
            permission = Integer.parseInt(doc.getPermissions().substring(0, 1));
        }

        Integer knowledgeBasePermissions = knowledgeBaseService.getUserKnowledgeBasePermissions(loginUser.getUserId(),
                doc.getKnowledgeBaseId());


        // 如果是知识库管理员
        if ( StringUtils.isNotNull(knowledgeBasePermissions) &&
                KnowledgeBasePermissions.MANAGE.getValue() == knowledgeBasePermissions) {
            int knowledgeBaseManagePermission = Integer.parseInt(doc.getPermissions().substring(1, 2));
            permission =  Math.max(permission, knowledgeBaseManagePermission);
        }
        else if (StringUtils.isNotNull(knowledgeBasePermissions) && KnowledgeBasePermissions.NO.getValue() != knowledgeBasePermissions) {
            int knowledgeBaseMemberPermission = Integer.parseInt(doc.getPermissions().substring(2, 3));
            permission = Math.max(permission, knowledgeBaseMemberPermission);
        }

        permission = Math.max(permission, Integer.parseInt(doc.getPermissions().substring(3, 4)));

        return DocPermissions.parse(permission);
    }

    @Override
    @RequiresDocPermissions(DocPermissions.READ)
    public DocVO getDocById(DocQueryParam queryParam) {
        DocVO docVO = this.baseMapper.selectDocById(queryParam.getDocId());
        if (StringUtils.isNull(docVO)) {
            throw new BusinessException("文档不存在");
        }
        docVO.setPermission(((DocPermissions)queryParam.getParams().get(RequiresDocPermissionsAspect.DOC_PERMISSIONS))
                .getValue());
        return docVO;
    }


    @Override
    @RequiresDocPermissions(DocPermissions.READ)
    public void queryDoc(DocRagQueryParam docRagQueryParam) throws IOException {

        DocVO docVO = this.getDocById(docRagQueryParam);
        HttpServletResponse response = ServletUtils.getResponse();
        response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        Writer writer = response.getWriter();
        Gson gson = new Gson();

        LoginUser loginUser = tokenUtil.getLoginUser();
        if (0 != docVO.getIndexStatus()) {
            writer.write(String.format("id: %s\nevent: message\ndata: %s\n\n", System.currentTimeMillis(),
                    gson.toJson(ResUtil.error(DocQueryVO.builder()
                            .status("failed")
                            .message("文档索引未生成，请先点击上方更多生成索引")
                            .build()))));
            writer.flush();
            return;
        }

        try {
            ragService.query(loginUser.getSysUser().getId(), docRagQueryParam.getDocId(), docRagQueryParam.getConversationId(), RagFileQueryReq.builder()
                            .file_hash(docVO.getHash())
                            .prompt(docRagQueryParam.getPrompt())
                            .file_name(docVO.getEnglishDocName() != null ?
                                    docVO.getEnglishDocName().replace(" ", "_") : "doc")
                            .author(docVO.getCreatorNickname())
                            .category("UNKNOWN")
                            .description("None")
                            .build(),
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
                        log.info(resJson);
                        try {
                            writer.write(String.format("id: %s\nevent: message\ndata: %s\n\n", System.currentTimeMillis(), resJson));
                            writer.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    });
        } catch (RagLimitException e) {
            writer.write(String.format("id: %s\nevent: message\ndata: %s\n\n", System.currentTimeMillis(), gson.toJson(ResUtil.success(DocQueryVO.builder()
                    .status("failed")
                    .message("您今日RAG使用次数已用完")
                    .build()))));
//            throw new BusinessException("您今日RAG使用次数已用完");
        }

//        Flux<RagFileQueryRes> resFlux = webClient.post()
//                .uri(aiServerAddress + "/api/rag/query")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(gson.toJson(RagFileQueryReq.builder()
//                        .file_hash(docVO.getHash())
//                        .prompt(docRagQueryParam.getPrompt())
//                        .file_name(docVO.getEnglishDocName().replace(" ", "_"))
//                        .author(docVO.getCreatorNickname())
//                        .category("UNKNOWN")
//                        .description("None")
//                        .build())))
//                .exchangeToFlux(res -> {
//                    if (res.statusCode().isError()) {
//                        throw new BusinessException("Rag查询失败");
//                    }
//                    else {
//                        return res.bodyToFlux(RagFileQueryRes.class);
//                    }
//                });
//
//        CountDownLatch latch = new CountDownLatch(1);
//
//        resFlux.subscribe(
//                value -> {
//                    String resJson = gson.toJson(ResUtil.success(DocQueryVO.builder()
//                            .status(value.getStatus())
//                            .message(value.getResult())
//                            .build()));
//                    log.info(resJson);
//                    try {
//                        writer.write(String.format("id: %s\nevent: message\ndata: %s\n\n", System.currentTimeMillis(), resJson));
//                        writer.flush();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                },
//                error -> {
//                    error.printStackTrace();
//                    latch.countDown();
//                },
//                latch::countDown
//        );
//
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
    }

    @Override
    public Doc selectDocById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @RequiresDocPermissions(DocPermissions.MANAGE)
    @Override
    public String indexDoc(DocIndexParam docIndexParam) {
        // 异步建立索引
        String destination = rocketMQProperties.getDocTopic() + ":" + DocTagsEnum.RAG_INDEX.name();
        rocketMQTemplate.asyncSend(destination, docIndexParam.getDocId(), RocketmqSendCallbackBuilder.commonCallback());
        return "SUCCESS";
    }

    @RequiresDocPermissions(DocPermissions.MANAGE)
    @Override
    public String deleteDoc(DocDeleteParam param) {
        int count = this.baseMapper.deleteById(param.getDocId());
        if (1 != count) {
            throw new BusinessException("删除失败");
        }
        return "SUCCESS";
    }

    @Override
    public DocVO getHomeDoc() {
        DocQueryParam docQueryParam = DocQueryParam.DocQueryParamBuilder()
                .docId(configService.getHomeDocId())
                .build();
        return SpringUtils.getAopProxy(this).getDocById(docQueryParam);
    }
}

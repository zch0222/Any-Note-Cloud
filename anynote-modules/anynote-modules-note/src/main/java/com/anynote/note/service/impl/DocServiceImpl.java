package com.anynote.note.service.impl;

import com.anynote.common.redis.service.ConfigService;
import com.anynote.common.rocketmq.callback.RocketmqSendCallbackBuilder;
import com.anynote.common.rocketmq.properties.RocketMQProperties;
import com.anynote.common.rocketmq.tags.DocTagsEnum;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.constant.FileConstants;
import com.anynote.core.constant.HuaweiOBSConstants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.StringUtils;
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
import com.anynote.note.enums.DocPermissions;
import com.anynote.note.enums.DocType;
import com.anynote.note.enums.KnowledgeBasePermissions;
import com.anynote.note.mapper.DocMapper;
import com.anynote.note.model.bo.*;
import com.anynote.note.model.vo.DocListVO;
import com.anynote.note.model.vo.DocVO;
import com.anynote.note.service.DocService;
import com.anynote.note.service.KnowledgeBaseService;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.po.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 文档服务器 IMPL
 * @author 称霸幼儿园
 */
@Service
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
        LoginUser loginUser = tokenUtil.getLoginUser();
        if (SysUser.isAdminX(loginUser.getSysUser().getRole())) {
            return DocPermissions.MANAGE;
        }
        LambdaQueryWrapper<Doc> docLambdaQueryWrapper = new LambdaQueryWrapper<>();
        docLambdaQueryWrapper
                .eq(Doc::getId, docId)
                .select(Doc::getPermissions, Doc::getCreateBy, Doc::getKnowledgeBaseId, Doc::getId);
        Doc doc = this.baseMapper.selectOne(docLambdaQueryWrapper);

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

}

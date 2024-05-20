package com.anynote.note.controller;

import com.anynote.common.security.annotation.InnerAuth;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.validation.annotation.Upload;
import com.anynote.core.validation.enums.FileType;
import com.anynote.core.web.model.bo.CreateResEntity;


import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.file.api.model.dto.DocUploadTempLinkDTO;
import com.anynote.note.api.model.po.Doc;
import com.anynote.note.model.bo.*;
import com.anynote.note.model.dto.CompleteDocUploadDTO;
import com.anynote.note.model.dto.DocListDTO;
import com.anynote.note.model.dto.DocRagQueryDTO;
import com.anynote.note.model.vo.DocListVO;
import com.anynote.note.api.model.vo.DocVO;
import com.anynote.note.service.DocService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * 文档 Controller
 * @author 称霸幼儿园
 */
@RestController
@RequestMapping("/docs")
@Validated
public class DocController {


    @Resource
    private DocService docService;

    @PostMapping("upload")
    public ResData<HuaweiOBSTemporarySignature> docUploadTempLink(@Validated @RequestBody DocUploadTempLinkDTO docUploadTempLinkDTO) {
        return ResUtil.success(docService.createDocUploadTempLink(DocUploadSignatureCreateParam
                .DocUploadSignatureCreateParamBuilder()
                .knowledgeBaseId(docUploadTempLinkDTO.getKnowledgeBaseId())
                .docName(docUploadTempLinkDTO.getFileName())
                .contentType(docUploadTempLinkDTO.getContentType()).build()));
    }

    @PutMapping("upload")
    public ResData<CreateResEntity> completeDocUpload(@Validated @RequestBody CompleteDocUploadDTO completeDocUploadDTO) {
        return ResUtil.success(docService.completeDocUpload(DocCreateParam.DocCreateParamBuilder()
                        .uploadId(completeDocUploadDTO.getUploadId())
                        .hash(completeDocUploadDTO.getHash())
                        .docName(completeDocUploadDTO.getDocName())
                        .knowledgeBaseId(completeDocUploadDTO.getKnowledgeBaseId())
                .build()));
    }

    @Upload(value = {FileType.PDF}, max = 500)
    @PostMapping("pdfs")
    public ResData<CreateResEntity> createPDF(@NotNull(message = "PDF文档不能为空") @RequestParam("pdf") CommonsMultipartFile pdf,
                                              @NotNull(message = "知识库ID不能为空") @RequestParam("knowledgeBaseId") Long knowledgeBaseId,
                                              @NotNull(message = "文件上传ID不能为空") @RequestParam("uploadId") String uploadId) {
        return ResUtil.success(docService.createPDF(PDFCreateParam.PDFCreateParamBuilder()
                        .knowledgeBaseId(knowledgeBaseId)
                        .uploadId(uploadId)
                        .pdf(pdf)
                .build()));
    }

    @GetMapping("")
    public ResData<PageBean<DocListVO>> getDocList(@Validated DocListDTO docListDTO) {
        return ResUtil.success(docService.getDocList(DocQueryParam.DocQueryParamBuilder()
                .page(docListDTO.getPage())
                .pageSize(docListDTO.getPageSize())
                .knowledgeBaseId(docListDTO.getKnowledgeBaseId())
                .build()));
    }

    @GetMapping("{id}")
    public ResData<DocVO> getDoc(@Validated @PathVariable @NotNull(message = "文档ID不能为空") Long id) {
        return ResUtil.success(docService.getDocById(DocQueryParam.DocQueryParamBuilder().docId(id).build()));
    }

    @InnerAuth
    @GetMapping("inner/{id}")
    public ResData<Doc> selectDocById(@Validated @PathVariable @NotNull(message = "文档ID不能为空") Long id) {
        return ResUtil.success(docService.selectDocById(id));
    }

    @PostMapping("{id}/query")
    public void ragQueryDoc(@Validated @PathVariable @NotNull(message = "文档ID不能为空") Long id,
                                           @RequestBody DocRagQueryDTO docRagQueryDTO) throws IOException {
        docService.queryDoc(DocRagQueryParam.DocRagQueryParamBuilder()
                .docId(id)
                .prompt(docRagQueryDTO.getPrompt())
                .conversationId(docRagQueryDTO.getConversationId())
                .build());
    }

    @PostMapping("{id}/index")
    public ResData<String> indexDoc(@Validated @PathVariable @NotNull(message = "文档ID不能为空") Long id) {
        DocIndexParam docIndexParam = new DocIndexParam();
        docIndexParam.setDocId(id);
        return ResUtil.success(docService.indexDoc(docIndexParam));
    }

    @DeleteMapping("{id}")
    public ResData<String> deleteDoc(@Validated @PathVariable("id") @NotNull(message = "文档ID不能为空") Long id) {
        DocDeleteParam docDeleteParam = new DocDeleteParam();
        docDeleteParam.setDocId(id);
        return ResUtil.success(docService.deleteDoc(docDeleteParam));
    }

    @GetMapping("home")
    public ResData<DocVO> getHomeDoc() {
        return ResUtil.success(docService.getHomeDoc());
    }

    @GetMapping("public/{id}")
    public ResData<DocVO> getPublicDoc(@Validated @PathVariable @NotNull(message = "文档ID不能为空") Long id) {
        return ResUtil.success(docService.getDocById(DocQueryParam.DocQueryParamBuilder().docId(id).build()));
    }
}

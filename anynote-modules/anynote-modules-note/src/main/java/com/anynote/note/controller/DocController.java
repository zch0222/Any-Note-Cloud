package com.anynote.note.controller;

import com.anynote.core.utils.ResUtil;
import com.anynote.core.validation.annotation.Upload;
import com.anynote.core.validation.enums.FileType;
import com.anynote.core.web.model.bo.CreateResEntity;


import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.model.dto.DocUploadTempLinkDTO;
import com.anynote.note.model.bo.PDFCreateParam;
import com.anynote.note.service.DocService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

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
    public ResData<CreateResEntity> docUploadTempLink(DocUploadTempLinkDTO docUploadTempLinkDTO) {
        return null;
    }

    @Upload(value = FileType.PDF, max = 500)
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

}

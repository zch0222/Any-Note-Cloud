package com.anynote.note.service;

import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.note.api.model.po.Doc;
import com.anynote.note.model.bo.PDFCreateParam;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档服务
 * @author 称霸幼儿园
 */
public interface DocService extends IService<Doc> {


    public CreateResEntity createPDF(PDFCreateParam pdfCreateParam);

    public HuaweiOBSTemporarySignature createDocUploadTempLink();
}

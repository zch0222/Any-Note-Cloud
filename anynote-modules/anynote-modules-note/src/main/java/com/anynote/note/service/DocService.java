package com.anynote.note.service;

import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.note.api.model.po.Doc;
import com.anynote.note.enums.DocPermissions;
import com.anynote.note.model.bo.DocCreateParam;
import com.anynote.note.model.bo.DocQueryParam;
import com.anynote.note.model.bo.DocUploadSignatureCreateParam;
import com.anynote.note.model.bo.PDFCreateParam;
import com.anynote.note.model.vo.DocListVO;
import com.anynote.note.model.vo.DocVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文档服务
 * @author 称霸幼儿园
 */
public interface DocService extends IService<Doc> {


    public CreateResEntity createPDF(PDFCreateParam pdfCreateParam);

    public HuaweiOBSTemporarySignature createDocUploadTempLink(DocUploadSignatureCreateParam docUploadSignatureCreateParam);

    public CreateResEntity completeDocUpload(DocCreateParam docCreateParam);

    public PageBean<DocListVO> getDocList(DocQueryParam queryParam);

    public DocPermissions getDocPermissions(Long docId);

    public DocVO getDocById(DocQueryParam queryParam);


}

package com.anynote.note.service;

import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.note.api.model.po.Doc;
import com.anynote.note.enums.DocPermissions;
import com.anynote.note.model.bo.*;
import com.anynote.note.model.vo.DocListVO;
import com.anynote.note.api.model.vo.DocVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

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

    public Doc selectDocById(Long id);

    public void queryDoc(DocRagQueryParam docRagQueryParam) throws IOException;

    public String indexDoc(DocIndexParam docIndexParam);

    public String deleteDoc(DocDeleteParam param);


}

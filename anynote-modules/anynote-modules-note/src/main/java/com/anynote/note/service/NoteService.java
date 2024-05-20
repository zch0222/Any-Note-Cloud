package com.anynote.note.service;

import com.anynote.common.elasticsearch.model.EsNoteIndex;
import com.anynote.common.elasticsearch.model.bo.SearchPageBean;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.note.api.model.po.Note;
import com.anynote.note.enums.NotePermissions;
import com.anynote.note.model.bo.*;
import com.anynote.note.model.dto.NoteSearchDTO;
import com.anynote.note.model.vo.NoteListVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 笔记服务
 * @author 称霸幼儿园
 */
public interface NoteService extends IService<Note> {


    /**
     * 获取最近更新的笔记
     * @param queryParam
     * @return
     */
    public PageBean<NoteListVO> getNoteList(NoteQueryParam queryParam);

    public PageBean<Note> getNotesByKnowledgeBaseId(NoteQueryParam queryParam);

    public Note getNoteById(NoteQueryParam queryParam);

    public String editNote(NoteUpdateParam updateParam);

    public Integer getNoteDataScope(Long noteId);

    public String deleteNote(NoteDeleteParam param);

    /**
     * 创建笔记
     * @return 新笔记的id
     */
    public Long createNote(NoteCreateParam param);

    public NotePermissions getNotePermissions(Long noteId);

    public MarkdownImage uploadNoteImage(NoteImageUploadParam uploadParam);

    /**
     * 创建笔记上传临时地址
     * @param createParam 创建参数
     * @return 华为OBS临时签名
     */
    public HuaweiOBSTemporarySignature getImageUploadTempSignature(NoteImageUploadSignatureCreateParam createParam);

    public String completeNoteImageUpload(NoteImageCompleteParam noteImageCompleteParam);

    public Integer submitNote(Long noteId);

    public SearchPageBean<EsNoteIndex> searchNote(NoteSearchDTO noteSearchDTO);


    public Note selectNoteById(NoteQueryParam queryParam);


}

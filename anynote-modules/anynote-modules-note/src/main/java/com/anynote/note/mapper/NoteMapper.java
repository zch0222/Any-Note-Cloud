package com.anynote.note.mapper;

import com.anynote.common.datascope.annotation.DataScopeInterceptor;
import com.anynote.note.api.model.po.Note;
import com.anynote.note.datascope.annotation.NoteDataScopeInterceptor;
import com.anynote.note.model.bo.NoteQueryParam;
import com.anynote.note.model.bo.NoteUpdateParam;
import com.anynote.note.model.vo.NoteListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 笔记 Mapper
 * @author 称霸幼儿园
 */
@Mapper
public interface NoteMapper extends BaseMapper<Note> {


    @DataScopeInterceptor
    @NoteDataScopeInterceptor
    public List<Note> selectNoteInfoList(NoteQueryParam queryParam);

    public List<NoteListVO> selectNoteList(NoteQueryParam queryParam);

    /**
     * 获取note id
     * @return
     */
//    @NoteDataScopeInterceptor
    public Note selectNoteById(NoteQueryParam queryParam);

//    @NoteDataScopeInterceptor
    public Integer updateNote(NoteUpdateParam updateParam);

    public Integer updateContent(NoteUpdateParam updateParam);
}

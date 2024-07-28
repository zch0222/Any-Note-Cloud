package com.anynote.note.service.impl;

import com.anynote.core.web.model.bo.PageBean;
import com.anynote.note.api.model.po.NoteTaskSubmissionRecord;
import com.anynote.note.datascope.annotation.RequiresNoteTaskPermissions;
import com.anynote.note.enums.NoteTaskPermissions;
import com.anynote.note.mapper.NoteTaskMapper;
import com.anynote.note.mapper.NoteTaskSubmissionRecordMapper;
import com.anynote.note.model.bo.NoteTaskSubmissionRecordQueryParam;
import com.anynote.note.model.dto.NoteTaskSubmissionRecordDTO;
import com.anynote.note.service.NoteTaskSubmissionRecordService;
import com.anynote.note.validate.annotation.PageValid;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 称霸幼儿园
 */
@Service
public class NoteTaskSubmissionRecordServiceImpl
        extends ServiceImpl<NoteTaskSubmissionRecordMapper, NoteTaskSubmissionRecord>
        implements NoteTaskSubmissionRecordService {

    @Autowired
    private NoteTaskMapper noteTaskMapper;


//    @Override
//    public PageBean<NoteTaskSubmissionRecordDTO> getNoteTaskSubmitRecords(Long noteTaskId, Integer page, Integer pageSize) {
//        LambdaQueryWrapper<NoteTask> noteTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        noteTaskLambdaQueryWrapper
//                .eq(NoteTask::getId, noteTaskId)
//                .select(NoteTask::getKnowledgeBaseId);
//        NoteTask noteTaskInfo = noteTaskMapper.selectOne(noteTaskLambdaQueryWrapper);
//        NoteTaskSubmissionRecordQueryParam noteTaskSubmissionRecordQueryParam =
//                new NoteTaskSubmissionRecordQueryParam();
//        noteTaskSubmissionRecordQueryParam.setNoteTaskId(noteTaskId);
//        noteTaskSubmissionRecordQueryParam.setId(noteTaskInfo.getKnowledgeBaseId());
//        noteTaskSubmissionRecordQueryParam.setPage(page);
//        noteTaskSubmissionRecordQueryParam.setPageSize(pageSize);
//        return noteTaskSubmissionRecordService.getNoteTaskSubmitRecords(noteTaskSubmissionRecordQueryParam);
//    }


    @RequiresNoteTaskPermissions(NoteTaskPermissions.MANAGE)
    @PageValid
    public PageBean<NoteTaskSubmissionRecordDTO> getNoteTaskSubmitRecords(NoteTaskSubmissionRecordQueryParam
                                                                                  queryParam) {
        PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize(), "update_time desc");
        List<NoteTaskSubmissionRecordDTO> noteTaskSubmissionRecordDTOList = this.baseMapper
                .selectNoteTaskSubmissionRecordList(queryParam);
        PageInfo<NoteTaskSubmissionRecordDTO> pageInfo = new PageInfo<>(noteTaskSubmissionRecordDTOList);
        return PageBean.<NoteTaskSubmissionRecordDTO>builder()
                .pages(pageInfo.getPages())
                .total(pageInfo.getTotal())
                .rows(noteTaskSubmissionRecordDTOList)
                .current(queryParam.getPage())
                .build();
    }


}

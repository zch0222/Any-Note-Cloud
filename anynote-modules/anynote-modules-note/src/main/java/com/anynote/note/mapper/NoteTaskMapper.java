package com.anynote.note.mapper;

import com.anynote.note.api.model.bo.NoteOperationCount;
import com.anynote.note.api.model.po.NoteTask;
import com.anynote.note.model.bo.NoteTaskAnalyzeQueryParam;
import com.anynote.note.model.bo.NoteTaskChartsSelectParam;
import com.anynote.note.model.bo.NoteTaskQueryParam;
import com.anynote.note.model.dto.MemberNoteTaskDTO;
import com.anynote.note.model.po.NoteTaskAnalyzePO;
import com.anynote.note.model.po.NoteTaskChartsPO;
import com.anynote.note.model.po.NoteTaskSubmissionTimePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 称霸幼儿园
 */
@Mapper
public interface NoteTaskMapper extends BaseMapper<NoteTask> {


    public List<MemberNoteTaskDTO> selectMemberNoteTaskList(NoteTaskQueryParam queryParam);

    public List<NoteOperationCount> selectNoteOperationCount(Long noteTaskId);

    public List<NoteTaskAnalyzePO> selectNoteTaskAnalyze(NoteTaskAnalyzeQueryParam queryParam);

    public List<NoteTaskChartsPO> selectNoteTaskCharts(NoteTaskChartsSelectParam param);

    public NoteTaskSubmissionTimePO selectNoteTaskSubmissionTime(@Param("noteTaskId") Long noteTaskId);


}

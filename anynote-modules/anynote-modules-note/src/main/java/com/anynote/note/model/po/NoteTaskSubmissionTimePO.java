package com.anynote.note.model.po;

import lombok.Data;

import java.util.Date;

/**
 * 笔记提交时间最早最晚值
 * @author 称霸幼儿园
 */
@Data
public class NoteTaskSubmissionTimePO {

    /**
     * 最早时间
     */
    private Date earliestTime;

    /**
     * 最晚时间
     */
    private Date latestTime;

}

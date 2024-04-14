package com.anynote.note.model.po;

import lombok.Data;

import java.util.Date;

@Data
public class NoteTaskChartsPO {

//    // 任务id
//    private Long noteTaskId;


    // 用户id
    private Long userId;
    // 笔记id
    private Long noteId;


//    // 笔记历史id
//    private Long noteHistoryId;
    // 笔记编辑次数
    private Integer editCount;
//    // 笔记提交时间
//    private Date submitTime;
//    // 提交状态 0.正常
//    private Integer status;


    // 笔记最后更新时间
    private Date noteLastUpdateTime;
    // 用户名
    private String username;
    // 昵称
    private String nickname;

//    private Date operationTime;
//
//    private Long operationId;

}

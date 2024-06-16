package com.anynote.notify.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeSSEData {
    // 0.心跳 1.通知
    private int type;


    private NoticeVO data;

}

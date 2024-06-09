package com.anynote.notify.api.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("ntc_user_notice")
@NoArgsConstructor
@AllArgsConstructor
public class UserNotice {

    private Long noticeId;

    private Long userId;

    /**
     * 0.未读 1.已读
     */
    private Integer status;
}

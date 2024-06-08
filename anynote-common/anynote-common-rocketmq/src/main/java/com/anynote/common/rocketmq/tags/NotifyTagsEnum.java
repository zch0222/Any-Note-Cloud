package com.anynote.common.rocketmq.tags;

/**
 * @author 称霸幼儿园
 */
public enum NotifyTagsEnum {

    NOTICE("消息推送");

    private final String description;

    NotifyTagsEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static String getNoticeTag(Long userId) {
        return NOTICE.name() + ":" + userId.toString();
    }
}

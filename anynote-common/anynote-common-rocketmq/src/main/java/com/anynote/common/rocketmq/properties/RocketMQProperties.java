package com.anynote.common.rocketmq.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author 称霸幼儿园
 */
@Component
@Data
@RefreshScope
@ConfigurationProperties(prefix = "anynote.data.rocketmq")
public class RocketMQProperties {

    private String noteTaskTopic;

    private String noteTaskGroup;

    private String noteTopic;

    private String noteGroup;

    private String docTopic;

    private String docGroup;

    private String ragTopic;

    private String ragGroup;

    private String aiChatTopic;

    private String aiChatGroup;

    private String notifyTopic;

    private String notifyGroup;

    private String notifyNoteGroup;
}

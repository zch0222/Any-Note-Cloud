package com.anynote.ai.test.rocketmq;


import com.anynote.common.rocketmq.callback.RocketmqSendCallbackBuilder;
import com.anynote.common.rocketmq.properties.RocketMQProperties;
import com.anynote.common.rocketmq.tags.NotifyTagsEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class NotifyTest {



    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Resource
    private RocketMQProperties rocketMQProperties;

    @Test
    void test() {
        String destination = rocketMQProperties.getNotifyGroup() + "-" + 2L + ":" + NotifyTagsEnum.getNoticeTag(2L);
        rocketMQTemplate.asyncSend(destination, "TEST", RocketmqSendCallbackBuilder.commonCallback());
    }
}

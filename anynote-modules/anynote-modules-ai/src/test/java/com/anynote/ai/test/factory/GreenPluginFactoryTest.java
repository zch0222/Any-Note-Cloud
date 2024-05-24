package com.anynote.ai.test.factory;


import com.anynote.ai.factory.GreenPluginFactory;
import com.anynote.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GreenPluginFactoryTest {

    @Resource
    private GreenPluginFactory greenPluginFactory;


    @Test
    void testLlmQueryModeration() throws Exception {
        greenPluginFactory.greenPlugin().llmQueryModeration("操你妈");
    }

}

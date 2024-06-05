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
    @Test
    void test() {
        int[] nums = {1,1,1,2,2,3};
        int count = nums.length;
        for (int i = 0; i < count-1; ++i) {
            while (nums[i] == nums[i+1]) {
                count--;
                for (int j = i+1; j < count-1; ++j) {
                    nums[j] = nums[j+1];
                }
            }
        }
        System.out.println(count);
    }

}

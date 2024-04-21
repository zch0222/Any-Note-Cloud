package com.anynote.ai.scheduled;

import com.anynote.common.redis.service.RedisService;
import com.anynote.core.utils.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

//@Component
@Slf4j
public class RagScheduledTasks {

    @Resource
    private RedisService redisService;

    // 每天凌晨0点执行
//    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyTask() {
        log.info("执行了定时任务: RagScheduledTasks dailyTask，现在时间：" + System.currentTimeMillis());
        System.out.println("执行了定时任务，现在时间：" + System.currentTimeMillis());
        Map<String, Object> map = redisService.getObjects("RAG:COUNT:" +
                DateUtils.getDateString(DateUtils.getYesterdayUsingCalendar()));
    }

}

package com.anynote.ai.nio.datascope.aspect;

import com.anynote.ai.api.model.po.WhisperTask;
import com.anynote.ai.nio.datascope.annotation.RequiresWhisperTaskPermissions;
import com.anynote.ai.nio.model.bo.WhisperTaskQueryParam;
import com.anynote.ai.nio.service.WhisperTaskService;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.auth.AuthException;
import com.anynote.core.utils.StringUtils;
import com.anynote.system.api.model.bo.LoginUser;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;

@Aspect
@Component
@Order(1)
@Slf4j
public class RequiresWhisperTaskPermissionsAspect {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private WhisperTaskService whisperTaskService;

    @Resource
    private Gson gson;

    @Around("@annotation(requiresWhisperTaskPermissions)")
    public Flux<Object> doBefore(ProceedingJoinPoint joinPoint, RequiresWhisperTaskPermissions requiresWhisperTaskPermissions) {
        WhisperTaskQueryParam whisperTaskQueryParam = this.getParam(joinPoint);
        LoginUser loginUser = tokenUtil.getLoginUser(whisperTaskQueryParam.getAccessToken());
        log.info(gson.toJson(whisperTaskQueryParam));

        Flux<Object> flux = null;

        try {
            flux = (Flux<Object>) joinPoint.proceed();
        } catch (Throwable e) {
            throw new BusinessException();
        }
//        return flux.subscribeOn(Schedulers.boundedElastic()).doOnSubscribe(subscription -> {
//            WhisperTask whisperTask = whisperTaskService.getBaseMapper()
//                    .selectById(whisperTaskQueryParam.getWhisperTaskId());
//            log.info(gson.toJson(whisperTask));
//            if (StringUtils.isNull(whisperTask)) {
//                log.error("任务不存在");
//                throw new BusinessException("任务不存在");
//            }
//            if (!loginUser.getUserId().equals(whisperTask.getCreateBy())) {
//                throw new AuthException("没有权限访问任务");
//            }
//        });
        Flux<Object> finalFlux = flux;
        return Mono.fromCallable(() -> {
            WhisperTask whisperTask = whisperTaskService.getBaseMapper()
                    .selectById(whisperTaskQueryParam.getWhisperTaskId());
            log.info(gson.toJson(whisperTask));
//            return Mono.error(new BusinessException("任务不存在"));
            if (StringUtils.isNull(whisperTask)) {
                log.error("任务不存在");
                throw new BusinessException("任务不存在");
//                return Mono.error();
            }
            if (!loginUser.getUserId().equals(whisperTask.getCreateBy())) {
                throw new AuthException("没有权限访问任务");
            }
            return Mono.empty();
        }).publishOn(Schedulers.boundedElastic()).flux().flatMap(data -> finalFlux);
    }

    private WhisperTaskQueryParam getParam(JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];

        if (StringUtils.isNull(params) || !(params instanceof WhisperTaskQueryParam)) {
            throw new BusinessException("请求参数异常");
        }

        return (WhisperTaskQueryParam) params;
    }
}

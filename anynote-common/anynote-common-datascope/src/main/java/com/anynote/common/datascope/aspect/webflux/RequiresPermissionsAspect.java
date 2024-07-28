package com.anynote.common.datascope.aspect.webflux;

import com.anynote.common.datascope.annotation.RequiresPermissions;
import com.anynote.common.datascope.constants.PermissionConstants;
import com.anynote.common.datascope.enums.PermissionRequestType;
import com.anynote.common.datascope.model.bo.PermissionAuthBO;
import com.anynote.common.datascope.service.PermissionService;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.condition.SpringWebfluxCondition;
import com.anynote.core.constant.SecurityConstants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.auth.AuthException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.QueryParam;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.system.api.RemoteSysPermissionRuleService;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.dto.GetSysPermissionRuleDTO;
import com.anynote.system.api.model.po.SysPermissionRule;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;

@Order(1)
@Aspect
@Component
@Slf4j
@Conditional(SpringWebfluxCondition.class)
public class RequiresPermissionsAspect {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private PermissionService permissionService;

    private QueryParam getQueryParam(ProceedingJoinPoint joinPoint, String queryParamName) {
        // 获取方法的参数名
        String[] argNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        // 获取方法的参数值
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < argNames.length; i++) {
            if (queryParamName.equals(argNames[i])) {
                // 找到指定名称的参数
                return (QueryParam) args[i];
            }
        }
        log.error(StringUtils.format("查询参数名称：{}不存在", queryParamName));
        throw new BusinessException("未知异常，请联系管理员");
    }

    @Around("@annotation(requiresPermissions)")
    public Object doAround(ProceedingJoinPoint joinPoint, RequiresPermissions requiresPermissions) {
        QueryParam queryParam = this.getQueryParam(joinPoint, requiresPermissions.queryParamName());
        String getterMethodName = "get" + Character.toUpperCase(requiresPermissions.paramIdName().charAt(0)) +
                requiresPermissions.paramIdName().substring(1);
        Long entityId = null;

        try {
            entityId = (Long) queryParam.getClass().getMethod(getterMethodName).invoke(queryParam);
        } catch (Exception e) {
            String errorMessage = StringUtils.format("获取id方法: {}不存在", getterMethodName);
            log.error(errorMessage, e);
            throw new BusinessException("未知异常，请联系管理员");
        }
        Long finalEntityId = entityId;
        Mono<PermissionAuthBO> permissionMono = Mono.deferContextual(ctx -> {
            String accessToken = ctx.get(SecurityConstants.ACCESS_TOKEN);
            LoginUser loginUser = tokenUtil.getLoginUser(accessToken);
            return Mono
                    .fromCallable(() -> permissionService
                            .auth(requiresPermissions.value(), finalEntityId, loginUser.getUserId()))
                    .publishOn(Schedulers.boundedElastic());
        });
        if (PermissionRequestType.SSE.getValue() == requiresPermissions.requestType()) {
            Flux<Object> flux = null;
            try {
                flux = (Flux<Object>) joinPoint.proceed();
            } catch (Throwable e) {
                log.error("获取请求Flux失败", e);
                throw new BusinessException("未知异常，请联系管理员");
            }
            Flux<Object> finalFlux = flux;
            return permissionMono
                    .flux()
                    .flatMap(permissionAuthBO -> {
                        if (permissionAuthBO.isAuthenticationSuccess()) {
                            return finalFlux
                                    .contextWrite(context -> context
                                            .put(PermissionConstants.PERMISSION_CONTEXT_KEY, permissionAuthBO.getPermission()));
                        }
                        return Flux.error(new AuthException());
                    });
        }
        else if (PermissionRequestType.NORMAL.getValue() == requiresPermissions.requestType()) {
            Mono<Object> mono = null;
            try {
                mono = (Mono<Object>) joinPoint.proceed();
            } catch (Throwable e) {
                log.error("获取请求Mono失败", e);
            }
            Mono<Object> finalMono = mono;
            return permissionMono
                    .flatMap(permissionAuthBO -> {
                        if (permissionAuthBO.isAuthenticationSuccess()) {
                            return finalMono.contextWrite(context -> context
                                    .put(PermissionConstants.PERMISSION_CONTEXT_KEY,
                                            permissionAuthBO.getPermission()));
                        }
                        return Mono.error(new AuthException());
                    });
        }
        log.error("请求类型requestType：{}为定义", requiresPermissions.requestType());
        throw new BusinessException("未知异常，请联系管理员");
    }

//    private PermissionEntity getEntity




}

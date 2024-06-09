package com.anynote.common.security.handler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import com.anynote.core.context.HttpContextHolder;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.auth.LoginException;
import com.anynote.core.exception.auth.TokenException;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.enums.HttpStatusEnum;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author 称霸幼儿园
 */
@Slf4j
//@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 业务异常
     * @param e
     * @param httpServletRequest
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResData handleBusinessException(BusinessException e, HttpServletRequest httpServletRequest) {
        log.error(e.getErrorMessage(), e);
        return ResData.error(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(LoginException.class)
    public ResData handleLoginException(LoginException e, HttpServletRequest httpServletRequest) {
        log.error(e.getErrorMessage(), e);
//        HttpContextHolder.setStatus(HttpStatusEnum.UNAUTHORIZED);
        return ResData.error(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(TokenException.class)
    public ResData handleTokenException(TokenException e, HttpServletRequest httpServletRequest) {
        log.error(e.getErrorMessage(), e);
        HttpContextHolder.setStatus(HttpStatusEnum.UNAUTHORIZED);
        return ResData.error(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResData handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest httpServletRequest) {
        log.error(e.getMessage(), e);
        return ResData.error(ResCode.USER_REQUEST_PARAM_ERROR, e.getConstraintViolations().iterator().next().getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResData handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResData.error(ResCode.USER_REQUEST_PARAM_ERROR, e.getFieldErrors().iterator().next().getDefaultMessage());
    }


    @ExceptionHandler(BindException.class)
    public ResData handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        return ResData.error(ResCode.USER_REQUEST_PARAM_ERROR, e.getFieldErrors().iterator().next().getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResData handleException(Exception e, HttpServletRequest httpServletRequest) {
        log.error(e.getMessage(), e);
        return ResUtil.error(ResCode.BUSINESS_ERROR, "未知错误，请联系管理员");
    }

}

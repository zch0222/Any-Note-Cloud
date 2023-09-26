package com.anynote.common.security.handler;

import javax.servlet.http.HttpServletRequest;

import com.anynote.core.context.HttpContextHolder;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.auth.LoginException;
import com.anynote.core.exception.auth.TokenException;
import com.anynote.core.web.enums.HttpStatusEnum;
import com.anynote.core.web.model.bo.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author 称霸幼儿园
 */
@Slf4j
@RestControllerAdvice
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

}

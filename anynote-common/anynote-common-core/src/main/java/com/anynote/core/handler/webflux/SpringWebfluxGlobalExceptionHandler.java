package com.anynote.core.handler.webflux;

import com.anynote.core.condition.SpringWebfluxCondition;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
@Conditional(SpringWebfluxCondition.class)
public class SpringWebfluxGlobalExceptionHandler {


    /**
     * 业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResData handleBusinessException(BusinessException e) {
        log.error(e.getErrorMessage(), e);
        return ResData.error(e.getErrorCode(), e.getErrorMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResData handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResUtil.error(ResCode.BUSINESS_ERROR, "未知错误，请联系管理员");
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResData handleConstraintViolationException(ConstraintViolationException e) {
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
}

package com.anynote.core.exception;

import com.anynote.core.web.enums.ResCode;

/**
 * 业务异常
 * @author 称霸幼儿园
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private ResCode code;

    /**
     * 错误信息
     */
    private String message;

    public BusinessException() {}

    public BusinessException(String message) {
        this.message = message;
        this.code = ResCode.BUSINESS_ERROR;
    }

    public BusinessException(String message, ResCode resCode) {
        this.message = message;
        this.code = resCode;
    }

    public BusinessException(ResCode resCode) {
        this.message = resCode.getMsg();
        this.code = resCode;
    }

    public ResCode getErrorCode() {
        return this.code;
    }

    public String getErrorMessage() {
        return this.message;
    }

    public void setErrorCode(ResCode resCode) {
        this.code = resCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.anynote.core.web.enums;

/**
 * AJAX返回code枚举类
 * @author 称霸幼儿园
 */
public enum ResCode {

    SUCCESS("00000", "成功", "操作成功"),
    USER_ERROR("A0001", "用户端错误", "操作错误"),
    USER_REGISTER_ERROR("A0100", "用户注册错误", "注册失败"),
    USER_AUTH_ERROR("A0300", "访问权限异常", "访问权限异常")
    ;

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误码描述
     */
    private final String description;

    /**
     * 返回消息
     */
    private final String msg;

    ResCode(String code, String description, String msg) {
        this.code = code;
        this.description = description;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMsg() {
        return this.msg;
    }
}

package com.anynote.core.web.enums;

/**
 * AJAX返回code枚举类
 * @author 称霸幼儿园
 */
public enum ResCode {

    SUCCESS("00000", "成功", "操作成功"),
    USER_ERROR("A0001", "用户端错误", "操作错误"),
    USER_REGISTER_ERROR("A0100", "用户注册错误", "注册失败"),
    USER_REQUEST_PARAM_ERROR("A0160", "用户端参数错误", "用户请求参数错误"),
    USER_NOT_FOUND("A0201", "用户账户不存在", "用户账户不存在"),
    USER_DISABLED("A0202", "账户被禁用", "对不起，账户被禁用"),
    USER_DELETED("A0203", "账户被删除", "对不起，账户被删除"),
    USER_PASSWORD_TRY_ERROR("A0211", "用户输入密码错误次数超限", "用户输入密码错误次数超限"),
    USER_IDENTITY_VERIFICATION_FAILED("A0220", "用户身份校验失败", "用户身份校验失败"),
    AUTH_ERROR("A0300", "访问权限异常", "访问权限异常"),
    UNAUTHORIZED_ERROR("A0301", "访问未授权", "访问未授权"),
    TOKEN_EXPIRED("A0311", "授权已过期", "授权已过期"),
    ACCESS_TOKEN_NOT_FOUND("A0350", "未携带AccessToken", "访问未授权 "),

    USER_PARAMETERS_ERROR("A0400", "用户请求参数错误", "用户请求参数错误"),
    INVALID_USER_INPUT("A0402", "无效的用户输入", "无效的用户输入"),
    INVALID_USER_INPUT_NOT_FOUND("A0404", "用户请求资源未找到", "用户请求资源未找到"),
    REQUIRED_PARAMETERS_NULL("A0410", "请求必填参数为空", "请求必填参数为空"),
    GREEN_ERROR("A0411", "内容安全检测不通过", "内容安全检测不通过"),

    USER_RESOURCE_EXCEPTION("A0600", "用户资源异常", "用户资源异常"),
    RESOURCE_DOMAINS_NOT_ALLOWED("A0606", "资源域名非法", "资源域名非法"),

    USER_UPLOAD_ERROR("A0700", "用户上传文件异常", "用户上传文件异常"),
    USER_UPLOAD_TYPE_NOT_MATCH("A0701", "用户上传文件类型不匹配", "上传文件类型不匹配"),
    USER_UPLOAD_SIZE_LIMIT_EXCEEDED("A0702", "用户上传文件太大", "上传文件太大"),
    USER_UPLOAD_FILE_NAME_LENGTH_LIMIT_EXCEEDED("A0703", "文件名长度超过限制", "文件名长度超过限制"),



    BUSINESS_ERROR("B0001", "系统执行出错 ", "系统执行出错, 请联系管理员"),
    INNER_SERVICE_ERROR("B0400", "系统内部调用失败", "系统内部调用失败"),
    INNER_SYSTEM_SERVICE_ERROR("B0401", "系统模块内部调用失败", "系统模块内部调用失败"),
    INNER_FILE_SERVICE_ERROR("B0402", "文件模块内部调用失败", "文件模块内部调用失败"),
    INNER_NOTE_SERVICE_ERROR("B0403", "笔记模块调用失败", "笔记模块调用失败"),

    CALLING_SERVICE_ERROR("C0001", "调用第三方服务出错", "调用第三方服务出错"),
    HUAWEI_OBS_UPLOAD_ERROR("C160", "华为OBS上传错误", "华为OBS上传错误"),
    HUAWEI_OBS_CREATE_TEMPORARY_SIGNATURE_FAIL("C161", "华为OBS创建临时签名失败", "华为OBS创建临时签名失败")
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

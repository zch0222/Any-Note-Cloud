package com.anynote.common.datascope.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {

    /**
     * 权限名称
     */
    String value();

    /**
     * 请求类型
     * 0. 非SSE
     * 1. SSE
     */
    int requestType() default 0;

    /**
     * 查询参数名称
     */
    String queryParamName() default "queryParam";

    /**
     * 查询参数中id参数名
     */
    String paramIdName();


}

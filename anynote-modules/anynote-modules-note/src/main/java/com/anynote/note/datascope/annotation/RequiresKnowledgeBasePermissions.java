package com.anynote.note.datascope.annotation;

import com.anynote.common.datascope.enums.Logical;
import com.anynote.note.api.enums.KnowledgeBasePermissions;

import java.lang.annotation.*;

/**
 * @author 称霸幼儿园
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresKnowledgeBasePermissions {
    /**
     * 需要校验的权限码
     */
    KnowledgeBasePermissions value();

    String message();

    /**
     * 验证模式：AND | OR，默认AND
     */
    Logical logical() default Logical.AND;
}

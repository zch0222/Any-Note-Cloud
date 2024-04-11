package com.anynote.note.datascope.annotation;

import com.anynote.note.enums.DocPermissions;

import java.lang.annotation.*;

/**
 * 需要的文档权限
 * @author 称霸幼儿园
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresDocPermissions {

    DocPermissions value();
}

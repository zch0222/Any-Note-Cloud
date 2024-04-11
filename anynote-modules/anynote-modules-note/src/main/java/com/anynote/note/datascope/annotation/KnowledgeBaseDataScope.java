package com.anynote.note.datascope.annotation;

import java.lang.annotation.*;

/**
 * 知识库数据权限注解
 * @author 称霸幼儿园
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KnowledgeBaseDataScope {


    /**
     * 表别名
     * @return
     */
    public String value();

    /**
     * 知识库表别名
     * @return
     */
    public String knowledgeBaseAlias() default "n_knowledge_base";
}

package com.anynote.note.enums;

import com.anynote.core.exception.BusinessException;

/**
 * 
 * @author 称霸幼儿园
 */
public enum DocPermissions {

    /**
     * 可管理(编辑、分享、删除)
     */
    MANAGE(7),

    /**
     * 编辑、阅读
     */
    EDIT(6),

    /**
     * 阅读
     */
    READ(4),

    /**
     * 没有权限
     */
    NO(0);

    private final int value;

    DocPermissions(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DocPermissions parse(int value) {
        if (7 == value) {
            return DocPermissions.MANAGE;
        }
        else if (6 == value) {
            return DocPermissions.EDIT;
        }
        else if (4 == value) {
            return DocPermissions.READ;
        }
        else if (0 == value) {
            return DocPermissions.NO;
        }
        throw new BusinessException("文档权限错误");
    }
}

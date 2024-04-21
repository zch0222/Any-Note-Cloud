package com.anynote.core.utils;

import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.ResData;

import java.util.HashMap;

/**
 * 返回工具
 * @author 称霸幼儿园
 */
public class ResUtil {

    /**
     * 成功
     * @param data
     * @return
     * @param <K>
     */
    public static <K> ResData<K> success(K data) {
        return new ResData<>(ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMsg(), data);
    }

    /**
     * 失败
     * @param resCode
     * @return
     * @param <K>
     */
    public static <K> ResData<K> error(ResCode resCode, K data) {
        return new ResData<>(resCode.getCode(), resCode.getMsg(), data);
    }

    public static <K> ResData<K> error(ResCode resCode, String msg, K data) {
        ResData<K> resData = new ResData<>(resCode.getCode(), msg, data);
        return resData;
    }

    public static ResData error(ResCode resCode) {
        return new ResData<>(resCode.getCode(), resCode.getMsg(), new String[0]);
    }

    public static <K> ResData<K> error(K data) {
        return new ResData<>(ResCode.BUSINESS_ERROR.getCode(), ResCode.BUSINESS_ERROR.getMsg(), data);
    }

    public static ResData error(ResCode resCode, String msg) {
        return new ResData<>(resCode.getCode(), msg, new HashMap<>());
    }
}

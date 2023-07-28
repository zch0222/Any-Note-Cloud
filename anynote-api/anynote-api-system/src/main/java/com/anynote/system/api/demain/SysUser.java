package com.anynote.system.api.demain;

import com.anynote.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户对象
 *
 * @author 称霸幼儿园
 */
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String username;

    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 性别 0男 1女 2未知
     */
    private Integer sex;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号状态 0正常 1停用
     */
    private Integer status;

    /**
     * 删除标记 1表示删除 0表示未删除
     */
    @TableField("is_delete")
    private Integer deleted;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

}

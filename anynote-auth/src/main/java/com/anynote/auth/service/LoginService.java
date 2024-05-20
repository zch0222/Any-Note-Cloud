package com.anynote.auth.service;

import com.anynote.auth.model.dto.RegisterDTO;
import com.anynote.auth.model.dto.ResetPasswordDTO;
import com.anynote.system.api.model.bo.LoginUser;

/**
 * 登录服务
 * @author 称霸幼儿园
 */
public interface LoginService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录用户信息
     */
    public LoginUser login(String username, String password);

    public LoginUser resetPassword(ResetPasswordDTO resetPasswordDTO);

    public LoginUser register(RegisterDTO registerDTO);
}

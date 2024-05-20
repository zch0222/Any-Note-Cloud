package com.anynote.auth.service.impl;

import com.anynote.auth.model.dto.RegisterDTO;
import com.anynote.auth.model.dto.ResetPasswordDTO;
import com.anynote.auth.service.LoginService;
import com.anynote.auth.service.PasswordService;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.common.security.utils.SecurityUtils;
import com.anynote.core.constant.UserConstants;
import com.anynote.core.enums.UserStatus;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.auth.LoginException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.system.api.RemoteUserService;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.bo.Token;
import com.anynote.system.api.model.dto.CreateUserDTO;
import com.anynote.system.api.model.po.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * 登录服务
 * @author 称霸幼儿园
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private TokenUtil tokenUtil;


    @Autowired
    private ThreadPoolTaskExecutor asyncExecutor;

    @Override
    public LoginUser login(String username, String password) {
        // 用户名或密码为空
        if(StringUtils.isAnyBlank(username, password)) {
            // 日志记录（需要添加）
            throw new LoginException("用户名/密码必须填写");
        }
        // 密码不在指定范围内
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new LoginException("用户密码不在指定范围内");
        }

        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new LoginException("用户名不在指定范围内");
        }

        ResData<LoginUser> userResult = remoteUserService.getUserInfo(username);

        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData())) {
            throw new LoginException(ResCode.USER_NOT_FOUND);
        }

        if (!ResData.SUCCESS.equals(userResult.getCode())) {
            throw new LoginException(userResult.getMsg());
        }

        LoginUser loginUser = userResult.getData();
        SysUser sysUser = loginUser.getSysUser();

        if (UserStatus.DELETED.getCode() == sysUser.getStatus()) {
            throw new LoginException(ResCode.USER_DELETED);
        }

        if (UserStatus.DISABLE.getCode() == sysUser.getStatus()) {
            throw new LoginException(ResCode.USER_DISABLED);
        }

        passwordService.validate(sysUser, password);

        Token token = tokenUtil.createToken(loginUser);

        return loginUser;
    }

    @Override
    public LoginUser resetPassword(ResetPasswordDTO resetPasswordDTO) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        ResData<SysUser> sysUserResData =
                remoteUserService.getSysUserById(loginUser.getSysUser().getId());


        if (StringUtils.isNull(sysUserResData) || StringUtils.isNull(sysUserResData.getData())) {
            throw new BusinessException("重置密码失败，请联系管理员");
        }

        if (!ResData.SUCCESS.equals(sysUserResData.getCode())) {
            throw new BusinessException("重置密码失败，请联系管理员");
        }


        SysUser sysUser = sysUserResData.getData();
        try {
            passwordService.validate(sysUser, resetPasswordDTO.getOldPassword());
        } catch (LoginException e) {
            e.printStackTrace();
            throw new BusinessException("重置密码失败，旧密码错误");
        }

        sysUser.setPassword(SecurityUtils.encryptPassword(resetPasswordDTO.getNewPassword()));

        ResData<Integer> updateUserRes = remoteUserService.updateSysUser(loginUser.getSysUser().getId(),
                sysUser);

        if (StringUtils.isNull(updateUserRes) || StringUtils.isNull(updateUserRes.getData())) {
            throw new BusinessException("重置密码失败，请联系管理员");
        }
        if (!ResData.SUCCESS.equals(updateUserRes.getCode())) {
            throw new BusinessException("重置密码失败，请联系管理员");
        }

        if (1 != updateUserRes.getData()) {
            throw new BusinessException("重置密码失败，请联系管理员");
        }
        asyncExecutor.submit(() -> {
            tokenUtil.removeTokens(loginUser.getSysUser().getUsername());
        });
        return this.login(sysUser.getUsername(), resetPasswordDTO.getNewPassword());
    }

    @Override
    public LoginUser register(RegisterDTO registerDTO) {
        LoginUser loginUser = null;
        try {
            loginUser = RemoteResDataUtil.getResData(remoteUserService.getUserInfo(registerDTO.getUsername()), "");
        } catch (BusinessException e) {
            log.info(e.getErrorMessage(), "注册用户名可用");
        }
        if (StringUtils.isNotNull(loginUser)) {
            throw new BusinessException("注册失败，用户名已存在");
        }

        CreateResEntity createResEntity = RemoteResDataUtil.getResData(remoteUserService.createUser(CreateUserDTO.builder()
                .username(registerDTO.getUsername())
                .nickname(registerDTO.getNickname())
                .password(registerDTO.getPassword())
                .email(registerDTO.getEmail())
                .phoneNumber("")
                .sex(registerDTO.getSex())
                .build()), "注册失败");

        if (StringUtils.isNull(createResEntity) || StringUtils.isNull(createResEntity.getId())) {
            throw new BusinessException("注册失败");
        }
        LoginUser resLoginUser = null;
        try {
            resLoginUser = this.login(registerDTO.getUsername(), registerDTO.getPassword());
        } catch (BusinessException e) {
            log.error(e.getErrorMessage());
            throw new BusinessException("注册失败");
        }

        return resLoginUser;
    }

    public static void main(String[] args) {
        System.out.println(SecurityUtils.encryptPassword("123456"));
    }
}

package com.anynote.auth.controller;

import com.anynote.auth.model.dto.LoginDTO;
import com.anynote.auth.model.dto.LoginRequestDTO;
import com.anynote.auth.model.dto.RegisterDTO;
import com.anynote.auth.model.dto.ResetPasswordDTO;
import com.anynote.auth.service.LoginService;
import com.anynote.auth.service.impl.LoginServiceImpl;
import com.anynote.common.security.utils.SecurityUtils;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.system.api.model.bo.LoginUser;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 认证 Controller
 * @author 称霸幼儿园
 */
@Api("Token Controller")
@RestController
@Validated
public class TokenController {

    @Autowired
    private LoginService loginService;

    @PostMapping("login")
    public ResData<LoginDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginUser loginUser = loginService.login(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        return ResData.success(new LoginDTO(loginUser));
    }

    @PostMapping("resetPassword")
    public ResData<LoginDTO> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        LoginUser loginUser = loginService.resetPassword(resetPasswordDTO);
        return ResData.success(new LoginDTO(loginUser));
    }

    @GetMapping("test")
    public ResData<String> test() {
        return ResData.success("OK");
    }

    @PostMapping("register")
    public ResData<LoginDTO> register(@RequestBody @Valid RegisterDTO registerDTO) {
        return ResUtil.success(new LoginDTO(loginService.register(registerDTO)));
    }

//    public static void main(String[] args) {
//        System.out.println(SecurityUtils.encryptPassword("123456"));
//    }
}

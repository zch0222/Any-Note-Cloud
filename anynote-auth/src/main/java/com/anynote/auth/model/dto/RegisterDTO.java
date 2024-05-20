package com.anynote.auth.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class RegisterDTO {

    @NotNull(message = "昵称不能为空")
    private String nickname;

    @NotNull(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,15}$",
            message = "用户名必须是8到15位，只能包含数字和字母")
    private String username;

    @NotNull(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,15}$",
            message = "密码必须是8到15位，且包含大写字母、小写字母和数字")
    private String password;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$|^$",
            message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "性别不能为空")
    @Max(value = 1, message = "性别错误")
    @Min(value = 0, message = "性别错误")
    private Integer sex;

}

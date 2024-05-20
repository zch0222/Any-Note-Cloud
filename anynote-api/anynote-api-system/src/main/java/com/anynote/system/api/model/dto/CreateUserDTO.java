package com.anynote.system.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {

    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    private String password;

    @NotNull(message = "昵称不能为空")
    private String nickname;

    private String email;

    private String phoneNumber;

    /**
     * 性别 0男 1女 2未知
     */
    private Integer sex;
}

package com.anynote.system.api;

import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.system.api.factory.RemoteUserFallbackFactory;
import com.anynote.system.api.model.bo.KnowledgeBaseImportUser;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.dto.CreateUserDTO;
import com.anynote.system.api.model.dto.KnowledgeBaseUserImportDTO;
import com.anynote.system.api.model.po.SysUser;
import com.anynote.system.api.model.vo.KnowledgeBaseUserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户服务远程调用
 *
 * @author 称霸幼儿园
 */
@FeignClient(contextId = "remoteUserService",
        value = ServiceNameConstants.SYSTEM_SERVICE,
        fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {

    @GetMapping("user/info/{username}")
    public ResData<LoginUser> getUserInfo(@PathVariable("username") String username);

    @PostMapping("user/bases/import")
    public ResData<KnowledgeBaseUserImportDTO> importKnowledgeBaseUser(@Valid @RequestBody KnowledgeBaseUserImportDTO
                                                                                      knowledgeBaseUserImportDTO);


    @GetMapping("user/bases")
    public ResData<PageBean<KnowledgeBaseUserVO>> getKnowledgeBaseUsers(@RequestParam("knowledgeBaseId") Long knowledgeBaseId,
                                                                        @RequestParam("page") Integer page,
                                                                        @RequestParam("pageSize") Integer pageSize,
                                                                        @RequestParam("username") String username);

    @GetMapping("user/{userId}")
    public ResData<SysUser> getSysUserById(@PathVariable("userId") @NotNull(message = "用户id不能为空") Long userId);

    @PutMapping("user/{userId}")
    public ResData<Integer> updateSysUser(@PathVariable("userId") @NotNull(message = "用户id不能为空") Long userId,
                                          @RequestBody SysUser sysUser);

    @GetMapping("user/{userId}/info")
    public ResData<SysUser> getSysUserInfoById(@PathVariable("userId") @NotNull(message = "用户id不能为空") Long userId);

    /**
     * 超级管理员获取用户列表
     * @param page 页码
     * @param pageSize 页面容量
     * @return
     */
    @GetMapping("user/manageList")
    public ResData<PageBean<SysUser>> getManageUserList(@RequestParam("page") Integer page,
                                                        @RequestParam("pageSize") Integer pageSize,
                                                        @RequestParam("username") String username);

    @PostMapping("user")
    public ResData<CreateResEntity> createUser(@RequestBody @Valid CreateUserDTO createUserDTO);
}

package com.anynote.common.datascope.service;

import com.anynote.common.datascope.model.bo.PermissionAuthBO;
import com.anynote.system.api.RemoteSysPermissionRuleService;
import com.anynote.system.api.model.po.SysPermissionRule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

public interface PermissionService {

    public SysPermissionRule getPermissionRuleByName(String permissionName);

    /**
     * 获取对实体的权限
     * @param permissionRule 权限规则
     * @param entityId 实体id
     * @param userId 用户id
     * @return 权限
     */
    public int getPermission(SysPermissionRule permissionRule, Long entityId, Long userId);

    /**
     * 权限校验
     * @param permissionName 权限规则名称
     * @param entityId 实体id
     * @param userId 用户id
     * @return 是否满足权限
     */
    public PermissionAuthBO auth(String permissionName, Long entityId, Long userId);

}

package com.anynote.system.service;

import com.anynote.system.api.model.dto.GetSysPermissionRuleDTO;
import com.anynote.system.api.model.po.SysPermissionRule;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysPermissionRuleService extends IService<SysPermissionRule> {

    public SysPermissionRule getSysPermissionRule(GetSysPermissionRuleDTO getSysPermissionRuleDTO);
}

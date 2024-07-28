package com.anynote.system.api;

import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.system.api.factory.RemoteSysPermissionRuleFallbackFactory;
import com.anynote.system.api.model.dto.GetSysPermissionRuleDTO;
import com.anynote.system.api.model.po.SysPermissionRule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@FeignClient(contextId = "remoteSysPermissionRuleService",
        value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSysPermissionRuleFallbackFactory.class)
public interface RemoteSysPermissionRuleService {

    @GetMapping("/permissionRules")
    public ResData<SysPermissionRule> getSysPermissionRule(@SpringQueryMap GetSysPermissionRuleDTO getSysPermissionRuleDTO,
                                                           @RequestHeader("from-source") String fromSource);

    @GetMapping("/permissionRules/{id}")
    public ResData<SysPermissionRule> getSysPermissionRuleById(@PathVariable("id") Long id);

}

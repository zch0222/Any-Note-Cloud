package com.anynote.system.api.factory;

import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.system.api.RemoteSysPermissionRuleService;
import com.anynote.system.api.model.dto.GetSysPermissionRuleDTO;
import com.anynote.system.api.model.po.SysPermissionRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 系统规则服务降级
 * @author 称霸幼儿园
 */
@Component
@Slf4j
public class RemoteSysPermissionRuleFallbackFactory implements FallbackFactory<RemoteSysPermissionRuleService> {


    @Override
    public RemoteSysPermissionRuleService create(Throwable cause) {
        log.error("系统规则服务调用失败", cause);
        return new RemoteSysPermissionRuleService() {
            @Override
            public ResData<SysPermissionRule> getSysPermissionRule(GetSysPermissionRuleDTO getSysPermissionRuleDTO,
                                                                   @RequestHeader("from-source") String fromSource) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }

            @Override
            public ResData<SysPermissionRule> getSysPermissionRuleById(Long id) {
                return ResData.error(ResCode.INNER_SYSTEM_SERVICE_ERROR);
            }
        };
    }
}

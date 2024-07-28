package com.anynote.note.test;

import com.anynote.common.datascope.service.PermissionService;
import com.anynote.core.constant.SecurityConstants;
import com.anynote.system.api.RemoteSysPermissionRuleService;
import com.anynote.system.api.model.dto.GetSysPermissionRuleDTO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PermissionServiceTest {


    @Resource
    private PermissionService permissionService;

    @Resource
    private RemoteSysPermissionRuleService remoteSysPermissionRuleService;

    @Resource
    Gson gson;

    @Test
    void getSysPermissionRule() {
        System.out.println(gson.toJson(remoteSysPermissionRuleService.getSysPermissionRuleById(1L)));
        System.out.println(gson.toJson(remoteSysPermissionRuleService.getSysPermissionRule(GetSysPermissionRuleDTO.builder()
                .permissionRuleName("ndoc:read").build(), SecurityConstants.INNER)));
    }

    @Test
    public void testGetPermission() {
        System.out.println(permissionService.auth("ndoc:read", 47L, 288L));
    }
}

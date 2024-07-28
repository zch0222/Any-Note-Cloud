package com.anynote.common.datascope.service.impl;

import com.alibaba.fastjson2.JSON;
import com.anynote.common.datascope.enums.PermissionEnum;
import com.anynote.common.datascope.mapper.PermissionRuleMapper;
import com.anynote.common.datascope.model.bo.EntityPermissionQueryParam;
import com.anynote.common.datascope.model.bo.PermissionAuthBO;
import com.anynote.common.datascope.model.po.EntityPermissionPO;
import com.anynote.common.datascope.service.PermissionService;
import com.anynote.core.constant.SecurityConstants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.RemoteResDataUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.note.api.RemoteKnowledgeBaseService;
import com.anynote.note.api.enums.KnowledgeBasePermissions;
import com.anynote.note.api.model.dto.GetUserKnowledgeBaseListDTO;
import com.anynote.note.api.model.po.UserKnowledgeBase;
import com.anynote.system.api.RemoteSysPermissionRuleService;
import com.anynote.system.api.enums.PermissionRuleKnowledgeBaseAssociationType;
import com.anynote.system.api.model.dto.GetSysPermissionRuleDTO;
import com.anynote.system.api.model.po.SysPermissionRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private RemoteSysPermissionRuleService remoteSysPermissionRuleService;

    @Resource
    private PermissionRuleMapper permissionRuleMapper;

    @Resource
    private RemoteKnowledgeBaseService remoteKnowledgeBaseService;


    @Override
    public SysPermissionRule getPermissionRuleByName(String permissionName) {
        return RemoteResDataUtil.getResData(remoteSysPermissionRuleService
                        .getSysPermissionRule(GetSysPermissionRuleDTO.builder()
                                .permissionRuleName(permissionName)
                                .build(), SecurityConstants.INNER),
                StringUtils.format(StringUtils.format("获取SysPermissionRule：{}失败", permissionName)));
    }

    private Integer getKnowledgeBasePermission(Long userId, List<Long> knowledgeBaseIds) {
        if (knowledgeBaseIds.isEmpty()) {
            return KnowledgeBasePermissions.NO.getValue();
        }
        List<UserKnowledgeBase> userKnowledgeBaseList = RemoteResDataUtil.getResData(remoteKnowledgeBaseService
                .getUserKnowledgeBaseList(GetUserKnowledgeBaseListDTO.builder()
                        .userId(userId)
                        .knowledgeBaseIds(knowledgeBaseIds).build(), SecurityConstants.INNER),
                StringUtils.format("获取知识库权限失败"));
        if (userKnowledgeBaseList.isEmpty()) {
            return KnowledgeBasePermissions.NO.getValue();
        }
        List<Integer> userKnowledgeBasePermissionList = userKnowledgeBaseList.stream()
                .map(UserKnowledgeBase::getPermissions)
                .collect(Collectors.toList());
        return Collections.min(userKnowledgeBasePermissionList);
    }

    private EntityPermissionPO getEntityPermissionPO(SysPermissionRule permissionRule, Long entityId) {
        if (PermissionRuleKnowledgeBaseAssociationType.ONE_TO_ONE.getValue() == permissionRule.getKnowledgeBaseAssociationType()) {
            return permissionRuleMapper.selectEntityPermissionOneToOne(EntityPermissionQueryParam.builder()
                    .entityId(entityId)
                    .entityTableName(permissionRule.getEntityTableName())
                    .entityIdFieldName(permissionRule.getEntityIdFieldName())
                    .permissionFieldName(permissionRule.getPermissionsFieldName())
                    .knowledgeBaseIdFieldName(permissionRule.getKnowledgeBaseIdFieldName())
                    .build());
        }
        else if (PermissionRuleKnowledgeBaseAssociationType.M_TO_N.getValue() == permissionRule.getKnowledgeBaseAssociationType()) {
            return null;
        }
        else if (PermissionRuleKnowledgeBaseAssociationType.NOT_ASSOCIATED.getValue() == permissionRule.getKnowledgeBaseAssociationType()) {
            return null;
        }
        log.error(StringUtils.format("knowledgeBaseAssociationType={}为定义", permissionRule.getKnowledgeBaseAssociationType()));
        throw new BusinessException("未知异常请，联系管理员");
    }



    @Override
    public int getPermission(SysPermissionRule permissionRule, Long entityId, Long userId) {
        EntityPermissionPO entityPermissionPO = this.getEntityPermissionPO(permissionRule, entityId);
        if (StringUtils.isNull(entityPermissionPO)) {
            log.error(StringUtils.format("permissionName={}, entityId={}, userId={}实体不存在",
                    permissionRule.getPermissionRuleName(), entityId, userId));
            throw new BusinessException("实体不存在");
        }
        int permission = 0;
        String permissions = entityPermissionPO.getPermissions();
        // 是创建者
        if (userId.equals(entityPermissionPO.getCreateBy())) {
            permission = Math.max(permission, Integer.parseInt(permissions.substring(0, 1)));
        }
        Integer knowledgeBasePermission = this.getKnowledgeBasePermission(userId, entityPermissionPO.getKnowledgeBaseIds());
        if (KnowledgeBasePermissions.MANAGE.getValue() == knowledgeBasePermission) {
            permission = Math.max(permission, Integer.parseInt(permissions.substring(1, 2)));
        }
        if (KnowledgeBasePermissions.READ.getValue() > knowledgeBasePermission) {
            permission = Math.max(permission, Integer.parseInt(permissions.substring(2, 3)));
        }
        // 如果只有知识库的阅读权限，则该用户最高只能从知识库获得阅读权限
        if (KnowledgeBasePermissions.READ.getValue() == knowledgeBasePermission) {
            permission = Math.max(permission, Math.min(PermissionEnum.READ.getValue(), Integer.parseInt(permissions.substring(2, 3))));
        }
        // 其他用户(非知识库用户)权限
        permission = Math.max(permission, Integer.parseInt(permissions.substring(3, 4)));
        // 匿名用户权限(未登录用户)
        permission = Math.max(permission, Integer.parseInt(permissions.substring(4, 5)));
        return permission;
    }

    @Override
    public PermissionAuthBO auth(String permissionName, Long entityId, Long userId) {
        SysPermissionRule permissionRule = this.getPermissionRuleByName(permissionName);
        int permission = this.getPermission(permissionRule, entityId, userId);
        return PermissionAuthBO.builder()
                .authenticationSuccess(permission >= permissionRule.getRequirePermission())
                .permission(permission)
                .build();
    }
}

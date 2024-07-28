package com.anynote.note.datascope.aspect;

import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.auth.AuthException;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.BaseEntity;
import com.anynote.note.datascope.annotation.RequiresKnowledgeBasePermissions;
import com.anynote.note.api.enums.KnowledgeBasePermissions;
import com.anynote.note.model.bo.KnowledgeBaseQueryParam;
import com.anynote.note.model.bo.NoteQueryParam;
import com.anynote.note.service.KnowledgeBaseService;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.po.SysUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 知识库权限切点
 * @author 称霸幼儿园
 */
@Aspect
@Component
@Order(4)
public class KnowledgeBasePermissionsAspect {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private TokenUtil tokenUtil;

    public static final String KNOWLEDGE_BASE_PERMISSIONS = "knowledgeBasePermissions";

    @Before("@annotation(knowledgeBasePermissions)")
    public void doBefore(JoinPoint joinPoint,
                         RequiresKnowledgeBasePermissions knowledgeBasePermissions) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        BaseEntity param = getKnowledgeBaseQueryParam(joinPoint);
        if (StringUtils.isNotNull(param) && param instanceof KnowledgeBaseQueryParam) {
            Integer permission = knowledgeBaseService.getUserKnowledgeBasePermissions(loginUser.getSysUser().getId(),
                    ((KnowledgeBaseQueryParam) param).getId());
            addKnowledgeBasePermissions(param, permission);
            auth(permission, knowledgeBasePermissions.value(), knowledgeBasePermissions.message());
        }
        else if (StringUtils.isNotNull(param) && param instanceof NoteQueryParam) {
            NoteQueryParam queryParam = (NoteQueryParam) param;
            Integer permission = null;

            SysUser sysUser = loginUser.getSysUser();
            if (StringUtils.isNotNull(queryParam.getKnowledgeBaseId())) {
                permission = knowledgeBaseService.getUserKnowledgeBasePermissions(sysUser.getId(), queryParam.getKnowledgeBaseId());
            }
            else if (StringUtils.isNotNull(queryParam.getId())) {
                permission = knowledgeBaseService.getUserKnowledgeBasePermissionsByNoteId(sysUser.getId(), queryParam.getId());
;            }
            addKnowledgeBasePermissions(param, permission);
            auth(permission, knowledgeBasePermissions.value(), knowledgeBasePermissions.message());
        }
    }


    /**
     * 验证权限
     * @param permission 用户具有的知识库权限
     * @param requiresPermissions 需要的知识库权限
     */
    private void auth(Integer permission, KnowledgeBasePermissions requiresPermissions, String message) {
        if (KnowledgeBasePermissions.NO.equals(requiresPermissions)) {
            return;
        }
        if (KnowledgeBasePermissions.READ.equals(requiresPermissions)) {
            if (StringUtils.isNull(permission) || permission > KnowledgeBasePermissions.READ.getValue()) {
                throw new AuthException(StringUtils.isNull(message) ? "没有权限查看知识库" : message,
                        ResCode.UNAUTHORIZED_ERROR);
            }
        }
        else if (KnowledgeBasePermissions.EDIT.equals(requiresPermissions)) {
            if (StringUtils.isNull(permission) || permission > KnowledgeBasePermissions.EDIT.getValue()) {
                throw new AuthException(StringUtils.isNull(message) ? "没有权限编辑知识库" : message,
                        ResCode.UNAUTHORIZED_ERROR);
            }
        }
        else if (KnowledgeBasePermissions.MANAGE.equals(requiresPermissions)) {
            if (StringUtils.isNull(permission) || permission > KnowledgeBasePermissions.MANAGE.getValue()) {
                throw  new AuthException(StringUtils.isNull(message) ? "没有权限管理知识库" : message,
                        ResCode.UNAUTHORIZED_ERROR);
            }
        }

    }

    private BaseEntity getKnowledgeBaseQueryParam(JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNull(params) || !(params instanceof BaseEntity)) {
            return null;
        }
        return (BaseEntity) params;
    }

    private void addKnowledgeBasePermissions(BaseEntity queryParam, Integer permissions) {
        if (StringUtils.isNull(queryParam.getParams())) {
            Map<String, Object> map = new HashMap<>();
            map.put(KNOWLEDGE_BASE_PERMISSIONS, permissions);
            queryParam.setParams(map);
        }
        else {
            queryParam.getParams().put(KNOWLEDGE_BASE_PERMISSIONS, permissions);
        }
    }
}

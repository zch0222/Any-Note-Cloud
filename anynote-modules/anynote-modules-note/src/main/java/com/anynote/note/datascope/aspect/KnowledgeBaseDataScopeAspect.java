package com.anynote.note.datascope.aspect;

import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.model.bo.BaseEntity;
import com.anynote.note.datascope.annotation.KnowledgeBaseDataScope;
import com.anynote.note.enums.KnowledgeBasePermissions;
import com.anynote.note.model.bo.KnowledgeBaseQueryParam;
import com.anynote.note.model.bo.NoteQueryParam;
import com.anynote.note.service.KnowledgeBaseService;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.po.SysUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Order(0)
@Component
public class KnowledgeBaseDataScopeAspect {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private KnowledgeBaseService knowledgeBaseService;


    private static final String KNOWLEDGE_BASE_DATA_SCOPE = "knowledgeBaseScope";

    @Before("@annotation(knowledgeBaseDataScope)")
    public void doBefore(JoinPoint joinPoint, KnowledgeBaseDataScope knowledgeBaseDataScope) {
        addEmptyNoteDataScope(joinPoint);
        KnowledgeBaseQueryParam queryParam = getQueryParam(joinPoint);
        if (StringUtils.isNull(queryParam)) {
            throw new BusinessException("未知异常，请联系管理员");
        }
        knowledgeBaseDataScopeFilter(knowledgeBaseDataScope.value(), queryParam);
    }

    private void knowledgeBaseDataScopeFilter(String alias, KnowledgeBaseQueryParam queryParam) {
        StringBuilder sqlString = new StringBuilder();
        LoginUser loginUser = tokenUtil.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        if (SysUser.isAdminX(sysUser.getRole())) {
            return;
        }
        Integer permission = knowledgeBaseService.getUserKnowledgeBasePermissions(sysUser.getId(), queryParam.getId());
        if (StringUtils.isNull(permission)) {
            sqlString.append(StringUtils.format(" OR {}.data_scope > 3", alias));
        }
        else if (permission < 3) {
            sqlString.append(StringUtils.format(" OR {}.data_scope = 3", alias));
            sqlString.append(StringUtils.format(" OR {}.create_by = {}", alias, sysUser.getId()));
            if (KnowledgeBasePermissions.MANAGE.getValue() == permission) {
                sqlString.append(StringUtils.format(" OR {}.data_scope = 2", alias));
            }
//                else if (KnowledgeBasePermissions.EDIT.getValue() == permission) {
//                    sqlString.append(StringUtils.format(" OR {}.data_scope = 1", noteAlias));
//                }
        }
        queryParam.getParams().put(KNOWLEDGE_BASE_DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
    }


    private void addEmptyNoteDataScope(final JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNull(params) || !(params instanceof KnowledgeBaseQueryParam)) {
            return;
        }
        BaseEntity baseEntity = (BaseEntity) joinPoint.getArgs()[0];
        if (StringUtils.isNull(baseEntity.getParams())) {
            Map<String, Object> map = new HashMap<>();
            map.put(KNOWLEDGE_BASE_DATA_SCOPE, "");
            baseEntity.setParams(map);
        }
    }


    private KnowledgeBaseQueryParam getQueryParam(JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNull(params) || !(params instanceof KnowledgeBaseQueryParam)) {
            return null;
        }
        return (KnowledgeBaseQueryParam) params;
    }

}

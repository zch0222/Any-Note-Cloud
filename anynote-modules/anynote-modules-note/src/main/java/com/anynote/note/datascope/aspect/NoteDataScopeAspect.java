package com.anynote.note.datascope.aspect;

import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.model.bo.BaseEntity;
import com.anynote.note.datascope.annotation.NoteDataScopeInterceptor;
import com.anynote.note.api.enums.KnowledgeBasePermissions;
import com.anynote.note.model.bo.NoteQueryParam;
import com.anynote.note.service.KnowledgeBaseService;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.po.SysUser;
import com.github.pagehelper.PageHelper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 笔记数据权限库
 *
 * 需要更加细分权限
 * @author 称霸幼儿园
 */
@Aspect
@Order(3)
@Component
public class NoteDataScopeAspect {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    public static final String NOTE_DATA_SCOPE = "noteDataScope";

    @Before("@annotation(noteDataScope)")
    public void doBefore(JoinPoint joinPoint, NoteDataScopeInterceptor noteDataScope) {
        addEmptyNoteDataScope(joinPoint);
        NoteQueryParam queryParam = getQueryParam(joinPoint);
        if (StringUtils.isNotNull(queryParam)) {
            noteDataScopeFilter(joinPoint, noteDataScope.noteAlias(), queryParam);
        }
    }

    private void noteDataScopeFilter(JoinPoint joinPoint, String noteAlias, NoteQueryParam queryParam) {
        StringBuilder sqlString = new StringBuilder();
        LoginUser loginUser = tokenUtil.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        // 超级管理员可以看到所有数据
        if (SysUser.isAdminX(sysUser.getRole())) {
            return;
        }
        Integer permission = null;
        if (StringUtils.isNotNull(queryParam.getKnowledgeBaseId())) {
            permission = knowledgeBaseService.getUserKnowledgeBasePermissions(sysUser.getId(), queryParam.getKnowledgeBaseId());
        }
        else if (StringUtils.isNotNull(queryParam.getId())) {
            permission = knowledgeBaseService.getUserKnowledgeBasePermissionsByNoteId(sysUser.getId(), queryParam.getId());
        }

        if (StringUtils.isNull(permission)) {
            sqlString.append(StringUtils.format(" OR {}.data_scope > 3", noteAlias));
        }
        else if (permission < 3) {
            sqlString.append(StringUtils.format(" OR {}.data_scope = 3", noteAlias));
            sqlString.append(StringUtils.format(" OR {}.create_by = {}", noteAlias, sysUser.getId()));
            if (KnowledgeBasePermissions.MANAGE.getValue() == permission) {
                sqlString.append(StringUtils.format(" OR {}.data_scope = 2", noteAlias));
            }
//                else if (KnowledgeBasePermissions.EDIT.getValue() == permission) {
//                    sqlString.append(StringUtils.format(" OR {}.data_scope = 1", noteAlias));
//                }
        }

        addDataScope(joinPoint, " AND (" + sqlString.substring(4) + ")");
        if (StringUtils.isNotNull(queryParam.getPage()) && StringUtils.isNotNull(queryParam.getPageSize())) {
            PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize(), "update_time desc");
        }

    }

    private void addEmptyNoteDataScope(final JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNull(params) || !(params instanceof NoteQueryParam)) {
            return;
        }
        BaseEntity baseEntity = (BaseEntity) joinPoint.getArgs()[0];
        if (StringUtils.isNull(baseEntity.getParams())) {
            Map<String, Object> map = new HashMap<>();
            map.put(NOTE_DATA_SCOPE, "");
            baseEntity.setParams(map);
        }
    }

    private void addDataScope(final JoinPoint joinPoint, String sql) {
        BaseEntity baseEntity = (BaseEntity) joinPoint.getArgs()[0];
        baseEntity.getParams().put(NOTE_DATA_SCOPE, sql);
    }


    private NoteQueryParam getQueryParam(JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNull(params) || !(params instanceof NoteQueryParam)) {
            return null;
        }
        return (NoteQueryParam) params;
    }


}

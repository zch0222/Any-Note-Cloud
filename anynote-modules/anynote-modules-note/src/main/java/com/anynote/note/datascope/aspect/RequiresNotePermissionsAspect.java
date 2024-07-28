package com.anynote.note.datascope.aspect;

import com.anynote.common.datascope.aspect.BasePermissionsAspect;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.auth.AuthException;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.enums.ResCode;
import com.anynote.note.datascope.annotation.RequiresNotePermissions;
import com.anynote.note.enums.NotePermissions;
import com.anynote.note.model.bo.NoteQueryParam;
import com.anynote.note.service.KnowledgeBaseService;
import com.anynote.note.service.NoteService;
import com.anynote.system.api.model.bo.LoginUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 笔记权限
 *
 * @author 称霸幼儿园
 */
@Aspect
@Order(2)
@Component
public class RequiresNotePermissionsAspect extends BasePermissionsAspect {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private NoteService noteService;

    public static final String NOTE_PERMISSIONS = "notePermissions";



    @Before("@annotation(requiresNotePermissions)")
    public void doBefore(JoinPoint joinPoint, RequiresNotePermissions requiresNotePermissions) {
        NoteQueryParam param = getParam(joinPoint);
        if (StringUtils.isNotNull(param)) {
            authPermissions(param, requiresNotePermissions);
        }
    }

    private void authPermissions(NoteQueryParam param, RequiresNotePermissions requiresNotePermissions) {
        LoginUser loginUser = tokenUtil.getLoginUser();
//        if (SysUser.isAdminX(loginUser.getSysUser().getRole())) {
//            return;
//        }

        NotePermissions permissions = noteService.getNotePermissions(param.getId());
        addNotePermissions(param, permissions);
        if (permissions.getValue() < requiresNotePermissions.value().getValue()) {
            if (NotePermissions.READ.equals(requiresNotePermissions.value())) {
                throw new AuthException("没有权限访问笔记", ResCode.UNAUTHORIZED_ERROR);
            }
            else if (NotePermissions.EDIT.equals(requiresNotePermissions.value())) {
                throw new AuthException("没有权限编辑笔记", ResCode.UNAUTHORIZED_ERROR);
            }
            else if (NotePermissions.MANAGE.equals(requiresNotePermissions.value())) {
                throw new AuthException("没有权限执行此操作", ResCode.UNAUTHORIZED_ERROR);
            }
        }

        param.setUpdateBy(loginUser.getSysUser().getId());
        param.setUpdateTime(new Date(System.currentTimeMillis()));
        addLoginUser(param, loginUser);

//        LoginUser loginUser = tokenUtil.getLoginUser();
//        SysUser sysUser = loginUser.getSysUser();
//
//        Note noteInfo = getNoteInfo(param.getId());
//        if (StringUtils.isNull(noteInfo)) {
//            throw new UserParamException("笔记不存在", ResCode.INVALID_USER_INPUT_NOT_FOUND);
//        }
//        Integer knowledgeBasePermission = knowledgeBaseService.getUserKnowledgeBasePermissionsByNoteId(sysUser.getId(), param.getId());
//        if (knowledgeBasePermission == null) {
//            throw new AuthException("没有权限访问笔记", ResCode.UNAUTHORIZED_ERROR);
//        }
//
//
//        if (NotePermissions.MANAGE.equals(requiresNotePermissions.value())) {
//            if (knowledgeBasePermission > KnowledgeBasePermissions.EDIT.getValue()) {
//                throw new AuthException("没有权限执行此操作", ResCode.INNER_SYSTEM_SERVICE_ERROR);
//            }
//            if (knowledgeBasePermission == KnowledgeBasePermissions.EDIT.getValue()
//             && !noteInfo.getCreateBy().equals(sysUser.getId())) {
//                throw new AuthException("没有权限执行此操作", ResCode.INNER_SYSTEM_SERVICE_ERROR);
//            }
//            if (NotePermissions.)
//        }
//        else if (NotePermissions.EDIT.equals(requiresNotePermissions.value())) {
//            if (knowledgeBasePermission > KnowledgeBasePermissions.EDIT.getValue()) {
//                throw new AuthException("没有权限编辑笔记", ResCode.UNAUTHORIZED_ERROR);
//            }
//            if (knowledgeBasePermission == KnowledgeBasePermissions.EDIT.getValue()
//              && noteInfo.getDataScope() < 3 && !noteInfo.getCreateBy().equals(sysUser.getId())) {
//                throw new AuthException("没有权限编辑笔记", ResCode.UNAUTHORIZED_ERROR);
//            }
//            if (knowledgeBasePermission == KnowledgeBasePermissions.MANAGE.getValue()
//              && noteInfo.getDataScope() < 2 && !noteInfo.getCreateBy().equals(sysUser.getId())) {
//                throw new AuthException("没有权限编辑笔记", ResCode.UNAUTHORIZED_ERROR);
//            }
//        }
    }



    private void addNotePermissions(NoteQueryParam queryParam, NotePermissions notePermissions) {
        if (StringUtils.isNull(queryParam.getParams())) {
            Map<String, Object> map = new HashMap<>();
            map.put(NOTE_PERMISSIONS, notePermissions);
            queryParam.setParams(map);
        }
        else {
            queryParam.getParams().put(NOTE_PERMISSIONS, notePermissions);
        }
    }

    private NoteQueryParam getParam(JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNull(params) || !(params instanceof NoteQueryParam)) {
            return null;
        }
        return (NoteQueryParam) params;
    }

//    private Note getNoteInfo(Long noteId) {
//        LambdaQueryWrapper<Note> noteLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        noteLambdaQueryWrapper
//                .eq(Note::getId, noteId)
//                .select(Note::getDataScope, Note::getCreateBy);
//        return noteService.getBaseMapper().selectOne(noteLambdaQueryWrapper);
//    }


}

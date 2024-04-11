package com.anynote.note.datascope.aspect;

import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.auth.AuthException;
import com.anynote.core.utils.StringUtils;
import com.anynote.note.datascope.annotation.RequiresDocPermissions;
import com.anynote.note.enums.DocPermissions;
import com.anynote.note.enums.NotePermissions;
import com.anynote.note.model.bo.DocQueryParam;
import com.anynote.note.model.bo.NoteQueryParam;
import com.anynote.note.service.DocService;
import com.anynote.system.api.model.bo.LoginUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Order(1)
public class RequiresDocPermissionsAspect {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private DocService docService;

    public final static String DOC_PERMISSIONS = "docPermissions";


    @Before("@annotation(requiresDocPermissions)")
    public void doBefore(JoinPoint joinPoint, RequiresDocPermissions requiresDocPermissions) {
        DocQueryParam queryParam = getParam(joinPoint);
        if (StringUtils.isNull(queryParam)) {
            throw new BusinessException("未知异常，请联系管理员");
        }
        authPermissions(queryParam, requiresDocPermissions.value());
    }

    private void authPermissions(DocQueryParam queryParam, DocPermissions reqPermissions) {
        DocPermissions permissions = docService.getDocPermissions(queryParam.getDocId());
        addNotePermissions(queryParam, permissions);
        if (permissions.getValue() < reqPermissions.getValue()) {
            throw new AuthException("没有权限执行操作");
        }
    }


    private DocQueryParam getParam(JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNull(params) || !(params instanceof DocQueryParam)) {
            return null;
        }
        return (DocQueryParam) params;
    }


    private void addNotePermissions(DocQueryParam queryParam, DocPermissions docPermissions) {
        if (StringUtils.isNull(queryParam.getParams())) {
            Map<String, Object> map = new HashMap<>();
            map.put(DOC_PERMISSIONS, docPermissions);
            queryParam.setParams(map);
        }
        else {
            queryParam.getParams().put(DOC_PERMISSIONS, docPermissions);
        }
    }

}

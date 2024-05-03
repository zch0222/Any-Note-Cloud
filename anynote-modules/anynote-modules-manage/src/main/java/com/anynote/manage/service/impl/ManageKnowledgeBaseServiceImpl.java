package com.anynote.manage.service.impl;

import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.manage.service.ManageKnowledgeBaseService;
import com.anynote.note.api.RemoteKnowledgeBaseService;
import com.anynote.note.api.model.dto.NoteKnowledgeBaseDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 称霸幼儿园
 */
@Service
public class ManageKnowledgeBaseServiceImpl implements ManageKnowledgeBaseService {

//    @Resource
//

    @Resource
    private RemoteKnowledgeBaseService remoteKnowledgeBaseService;


    @Override
    public PageBean<NoteKnowledgeBaseDTO> getKnowledgeBaseList(Integer page, Integer pageSize, Integer type, Integer status, Long organizationId) {
        ResData<PageBean<NoteKnowledgeBaseDTO>> resData =
                remoteKnowledgeBaseService.getManagerKnowledgeBases(page, pageSize, type, status, organizationId);

        if (StringUtils.isNull(resData) || StringUtils.isNull(resData.getData())) {
            throw new BusinessException("获取知识库列表失败");
        }

        if (!ResData.SUCCESS.equals(resData.getCode())) {
            throw new BusinessException("获取知识库列表失败");
        }

        return resData.getData();
    }
}

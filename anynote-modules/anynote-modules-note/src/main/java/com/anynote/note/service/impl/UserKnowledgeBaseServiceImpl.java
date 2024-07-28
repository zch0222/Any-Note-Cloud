package com.anynote.note.service.impl;

import com.anynote.note.api.model.dto.GetUserKnowledgeBaseListDTO;
import com.anynote.note.api.model.po.UserKnowledgeBase;
import com.anynote.note.mapper.UserKnowledgeBaseMapper;
import com.anynote.note.service.UserKnowledgeBaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserKnowledgeBaseServiceImpl extends ServiceImpl<UserKnowledgeBaseMapper, UserKnowledgeBase>
        implements UserKnowledgeBaseService {


    @Override
    public List<UserKnowledgeBase> getUserKnowledgeBaseList(GetUserKnowledgeBaseListDTO getUserKnowledgeBaseListDTO) {
        if (getUserKnowledgeBaseListDTO.getKnowledgeBaseIds().isEmpty()) {
            return Collections.emptyList();
        }
        return this.list(new LambdaQueryWrapper<UserKnowledgeBase>()
                .eq(UserKnowledgeBase::getUserId, getUserKnowledgeBaseListDTO.getUserId())
                .in(UserKnowledgeBase::getKnowledgeBaseId, getUserKnowledgeBaseListDTO.getKnowledgeBaseIds()));
    }
}

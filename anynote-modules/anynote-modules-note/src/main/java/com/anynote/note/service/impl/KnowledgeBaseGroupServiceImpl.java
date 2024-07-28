package com.anynote.note.service.impl;

import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.note.api.model.po.KnowledgeBaseGroup;
import com.anynote.note.datascope.annotation.RequiresKnowledgeBasePermissions;
import com.anynote.note.api.enums.KnowledgeBasePermissions;
import com.anynote.note.mapper.KnowledgeBaseGroupMapper;
import com.anynote.note.model.bo.KnowledgeBaseGroupCreateParam;
import com.anynote.note.model.bo.KnowledgeBaseGroupQueryParam;
import com.anynote.note.service.KnowledgeBaseGroupService;
import com.anynote.system.api.model.bo.LoginUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 称霸幼儿园
 */
@Service
public class KnowledgeBaseGroupServiceImpl extends ServiceImpl<KnowledgeBaseGroupMapper, KnowledgeBaseGroup>
        implements KnowledgeBaseGroupService {

    @Autowired
    private TokenUtil tokenUtil;


    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.READ, message = "没有权限获取分组")
    @Override
    public List<KnowledgeBaseGroup> getKnowledgeBaseGroups(KnowledgeBaseGroupQueryParam groupQueryParam) {
        LambdaQueryWrapper<KnowledgeBaseGroup> knowledgeBaseGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        knowledgeBaseGroupLambdaQueryWrapper
                .eq(StringUtils.isNotNull(groupQueryParam.getKnowledgeBaseId()), KnowledgeBaseGroup::getKnowledgeBaseId, groupQueryParam.getKnowledgeBaseId())
                .eq(StringUtils.isNotNull(groupQueryParam.getKnowledgeBaseGroupId()), KnowledgeBaseGroup::getId, groupQueryParam.getKnowledgeBaseGroupId())
                .eq(StringUtils.isNotNull(groupQueryParam.getKnowledgeBaseGroupParentId()), KnowledgeBaseGroup::getParentId, groupQueryParam.getKnowledgeBaseGroupParentId())
                .orderByAsc(KnowledgeBaseGroup::getOrderNum);
        return this.baseMapper.selectList(knowledgeBaseGroupLambdaQueryWrapper);
    }

    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "没有权限创建分组")
    @Override
    public Long createKnowledgeBaseGroup(KnowledgeBaseGroupCreateParam groupCreateParam) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        LambdaQueryWrapper<KnowledgeBaseGroup> currentGroupQueryWrapper = new LambdaQueryWrapper<>();
        currentGroupQueryWrapper
                .eq(KnowledgeBaseGroup::getKnowledgeBaseId, groupCreateParam.getKnowledgeBaseId())
                .eq(KnowledgeBaseGroup::getParentId, 0L);
        Long currentCount = this.baseMapper.selectCount(currentGroupQueryWrapper);
        KnowledgeBaseGroup knowledgeBaseGroup = KnowledgeBaseGroup.builder()
                .groupName(groupCreateParam.getGroupName())
                .knowledgeBaseId(groupCreateParam.getKnowledgeBaseId())
                .orderNum(currentCount + 1)
                .status(0)
                .build();
        Date date = new Date();
        knowledgeBaseGroup.setCreateBy(loginUser.getSysUser().getId());
        knowledgeBaseGroup.setCreateTime(date);
        knowledgeBaseGroup.setUpdateBy(loginUser.getSysUser().getId());
        knowledgeBaseGroup.setUpdateTime(date);
        this.baseMapper.insert(knowledgeBaseGroup);
        return knowledgeBaseGroup.getId();
    }
}

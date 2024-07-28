package com.anynote.system.service.impl;

import com.anynote.core.utils.StringUtils;
import com.anynote.system.api.model.dto.GetSysPermissionRuleDTO;
import com.anynote.system.api.model.po.SysPermissionRule;
import com.anynote.system.mapper.SysPermissionRuleMapper;
import com.anynote.system.service.SysPermissionRuleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysPermissionRuleServiceImpl extends ServiceImpl<SysPermissionRuleMapper, SysPermissionRule>
        implements SysPermissionRuleService {


    @Override
    public SysPermissionRule getSysPermissionRule(GetSysPermissionRuleDTO getSysPermissionRuleDTO) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<SysPermissionRule>()
                .eq(StringUtils.isNotNull(getSysPermissionRuleDTO.getPermissionRuleName()),
                        SysPermissionRule::getPermissionRuleName, getSysPermissionRuleDTO.getPermissionRuleName()));
    }
}

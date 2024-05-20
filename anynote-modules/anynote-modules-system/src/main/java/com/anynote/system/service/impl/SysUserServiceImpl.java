package com.anynote.system.service.impl;

import com.anynote.common.security.token.TokenUtil;
import com.anynote.common.security.utils.SecurityUtils;
import com.anynote.core.constant.Constants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.system.api.enums.KnowledgeBaseUserImportErrorType;
import com.anynote.system.api.model.bo.KnowledgeBaseImportUser;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.dto.ImportFailUser;
import com.anynote.system.api.model.dto.KnowledgeBaseUserImportDTO;
import com.anynote.system.api.model.po.SysUser;
import com.anynote.system.api.model.vo.KnowledgeBaseUserVO;
import com.anynote.system.mapper.SysUserMapper;
import com.anynote.system.api.model.bo.SysUserQueryParam;
import com.anynote.system.api.model.dto.CreateUserDTO;
import com.anynote.system.model.dto.ResetPasswordDTO;
import com.anynote.system.service.SysOrganizationService;
import com.anynote.system.service.SysPermissionService;
import com.anynote.system.service.SysRoleService;
import com.anynote.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务
 * @author 称霸幼儿园
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysPermissionService sysPermissionService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysOrganizationService sysOrganizationService;

    @Autowired
    private ThreadPoolTaskExecutor asyncExecutor;

    @Resource
    private TokenUtil tokenUtil;

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return
     */
    @Override
    public LoginUser getUserInfo(String username) {
        SysUser sysUser = this.selectUserByUserName(username);
        if (StringUtils.isNull(sysUser)) {
            throw new BusinessException(ResCode.USER_NOT_FOUND);
        }

        // 获取角色的权限列表
        Set<String> permissions = sysPermissionService.getRolePermission(sysUser);

        String roleKey = sysRoleService.selectRoleKeysByUserId(sysUser.getId());

        return new LoginUser(sysUser, permissions, roleKey, true);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return this.baseMapper.selectUserByUserName(userName);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public KnowledgeBaseUserImportDTO importKnowledgeBaseUser(KnowledgeBaseUserImportDTO knowledgeBaseUserImportDTO) {
//        List<String> failUserNameList = new ArrayList<>();
        List<ImportFailUser> failUserList = new ArrayList<>();
        String password = RandomStringUtils.randomNumeric(10);
        List<KnowledgeBaseImportUser> knowledgeBaseImportUserList = knowledgeBaseUserImportDTO.getKnowledgeBaseImportUserList().stream()
                .map(knowledgeBaseImportUser -> {
                    SysUser sysUser = new SysUser();
                    sysUser.setUsername(knowledgeBaseImportUser.getUsername());
                    sysUser.setNickname(knowledgeBaseImportUser.getNickname());
                    sysUser.setCreateBy(knowledgeBaseImportUser.getCreateBy());
                    sysUser.setCreateTime(knowledgeBaseImportUser.getCreateTime());
                    sysUser.setUpdateBy(knowledgeBaseImportUser.getUpdateBy());
                    sysUser.setUpdateTime(knowledgeBaseImportUser.getUpdateTime());
                    sysUser.setStatus(0);
                    sysUser.setSex(2);
                    sysUser.setDeleted(0);
                    sysUser.setPassword(SecurityUtils.encryptPassword(password));
                    if (false == checkUsername(sysUser.getUsername())) {
                        knowledgeBaseImportUser.setPassword("用户名已经存在，邀请进入知识库");
                        knowledgeBaseImportUser.setUserId(this.getUserIdByUserName(sysUser.getUsername()));
                    }
                    else {
                        try {
                            this.baseMapper.insert(sysUser);
                            asyncExecutor.submit(() -> associateUserRole(sysUser.getId(), 2L));
                            sysUser.setPassword(password);
                            knowledgeBaseImportUser.setPassword(password);
                            knowledgeBaseImportUser.setUserId(sysUser.getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                            failUserList.add(ImportFailUser.builder()
                                    .username(knowledgeBaseImportUser.getUsername())
                                    .reason(KnowledgeBaseUserImportErrorType.REGISTRATION_FAILED.getValue())
                                    .build());
//                            failUserNameList.add(knowledgeBaseImportUser.getUsername());
//                            failCount[0] = failCount[0] + 1;
                        }
                    }
                    return knowledgeBaseImportUser;
                }).collect(Collectors.toList());
        // 异步执行
        asyncExecutor.submit(() -> {
            sysOrganizationService.associateKnowledgeUserOrganization(knowledgeBaseImportUserList);
        });
//        knowledgeBaseUserImportDTO.setFailUserNameList(failUserNameList);
        knowledgeBaseUserImportDTO.setFailUserList(failUserList);
        return knowledgeBaseUserImportDTO;
    }

    @Override
    public Integer associateUserRole(Long userId, Long roleId) {
        return this.baseMapper.insertUserRole(userId, roleId);
    }

    private boolean checkUsername(String username) {
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper
                .eq(SysUser::getUsername, username)
                .select(SysUser::getUsername);
        return StringUtils.isNull(this.baseMapper.selectOne(sysUserLambdaQueryWrapper));
    }

    private Long getUserIdByUserName(String username) {
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper
                .eq(SysUser::getUsername, username)
                .select(SysUser::getId);
        SysUser sysUser = this.baseMapper.selectOne(sysUserLambdaQueryWrapper);
        if (StringUtils.isNull(sysUser)) {
            throw new BusinessException("用户名不存在", ResCode.USER_REQUEST_PARAM_ERROR);
        }
        return sysUser.getId();
    }

    @Override
    public PageBean<KnowledgeBaseUserVO> getKnowledgeBaseUsers(Long knowledgeBaseId, Integer page, Integer pageSize, String username) {
        PageHelper.startPage(page, pageSize, "user_id asc");
        List<KnowledgeBaseUserVO> knowledgeBaseUserVOList = this.baseMapper.selectKnowledgeBaseUsers(knowledgeBaseId, username);
        PageInfo<KnowledgeBaseUserVO> pageInfo = new PageInfo<>(knowledgeBaseUserVOList);
        return PageBean.<KnowledgeBaseUserVO>builder()
                .rows(knowledgeBaseUserVOList)
                .pages(pageInfo.getPages())
                .total(pageInfo.getTotal())
                .build();
    }

    @Override
    public SysUser getSysUserById(Long userId) {
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper
                .eq(SysUser::getId, userId)
                .select(SysUser::getUsername, SysUser::getPassword);
        return this.baseMapper.selectOne(sysUserLambdaQueryWrapper);
    }

    @Override
    public Integer updateSysUser(SysUser sysUser) {
        return this.baseMapper.updateById(sysUser);
    }

    @Override
    public PageBean<SysUser> getManageUserList(SysUserQueryParam queryParam) {
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper
                        .eq(StringUtils.isNotNull(queryParam.getUserId()), SysUser::getId, queryParam.getUserId())
                        .eq(StringUtils.isNotEmpty(queryParam.getUsername()), SysUser::getUsername, queryParam.getUsername());
        PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize(), "id DESC");
        List<SysUser> rows = this.baseMapper.selectList(sysUserLambdaQueryWrapper);
        PageInfo<SysUser> pageInfo = new PageInfo<>(rows);
        return PageBean.<SysUser>builder()
                .rows(rows)
                .current(queryParam.getPage())
                .pages(pageInfo.getPages())
                .total(pageInfo.getTotal())
                .build();
    }


    @Override
    public SysUser getSysUserInfoById(Long userId) {
        List<SysUser> sysUserList = this.baseMapper.selectSysUser(SysUserQueryParam.builder()
                        .userId(userId)
                .build());
        if (sysUserList.isEmpty()) {
            throw new BusinessException("用户不存在", ResCode.INVALID_USER_INPUT_NOT_FOUND);
        }
        if (sysUserList.size() > 1) {
            throw new BusinessException("获取用户信息失败");
        }
        SysUser sysUser = sysUserList.get(0);
        sysUser.setPassword("");
        return sysUser;
    }

    @Override
    public SysUser getMyUserInfo() {
        LoginUser loginUser = tokenUtil.getLoginUser();
        return this.getSysUserInfoById(loginUser.getSysUser().getId());
    }

    @Override
    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
        SysUser sysUser = new SysUser();
        sysUser.setId(resetPasswordDTO.getUserId());
        sysUser.setPassword(SecurityUtils.encryptPassword(resetPasswordDTO.getNewPassword()));
        int res = this.updateSysUser(sysUser);
        if (res == 1) {
            return Constants.SUCCESS_RES;
        }
        throw new BusinessException("重置密码失败，请联系管理员");
    }


    @Override
    public SysUser getPublicUserInfoByUsername(String username) {
        List<SysUser> sysUserList = this.baseMapper.selectSysUser(SysUserQueryParam.builder()
                .username(username).build());
        if (sysUserList.size() != 1) {
            throw new BusinessException("查询用户信息失败");
        }
        SysUser sysUser = sysUserList.get(0);
        sysUser.setPassword("");
        return sysUser;
    }

    @Override
    public Long createUser(CreateUserDTO createUserDTO) {
        Date date = new Date();
        SysUser sysUser = new SysUser();
        sysUser.setUsername(createUserDTO.getUsername());
        sysUser.setNickname(createUserDTO.getNickname());
        sysUser.setPassword(createUserDTO.getPassword());
        sysUser.setEmail(createUserDTO.getEmail());
        sysUser.setPhoneNumber(createUserDTO.getPhoneNumber());
        sysUser.setSex(createUserDTO.getSex());
        sysUser.setPassword(SecurityUtils.encryptPassword(createUserDTO.getPassword()));
        sysUser.setStatus(0);
        sysUser.setDeleted(0);
        sysUser.setLoginDate(date);
        sysUser.setCreateBy(0L);
        sysUser.setCreateTime(date);
        sysUser.setUpdateBy(0L);
        sysUser.setUpdateTime(date);
        int count = this.baseMapper.insert(sysUser);
        if (1 != count) {
            throw new BusinessException("创建用户失败");
        }
        this.associateUserRole(sysUser.getId(), 4L);
        return sysUser.getId();
    }
}

package com.anynote.note.service.impl;

import com.anynote.common.datascope.annotation.DataScope;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.constant.Constants;
import com.anynote.core.constant.FileConstants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.user.UserParamException;
import com.anynote.core.utils.MultipartFileUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.RemoteFileService;
import com.anynote.file.api.model.bo.FileDTO;
import com.anynote.note.api.model.po.UserKnowledgeBase;
import com.anynote.note.mapper.UserKnowledgeBaseMapper;
import com.anynote.note.model.bo.*;
import com.anynote.note.validate.annotation.PageValid;
import com.anynote.system.api.RemoteUserService;
import com.anynote.system.api.model.bo.KnowledgeBaseImportUser;
import com.anynote.note.api.model.po.NoteKnowledgeBase;
import com.anynote.note.datascope.annotation.RequiresKnowledgeBasePermissions;
import com.anynote.note.datascope.aspect.KnowledgeBasePermissionsAspect;
import com.anynote.note.enums.KnowledgeBasePermissions;
import com.anynote.note.mapper.KnowledgeBaseMapper;
import com.anynote.note.model.dto.KnowledgeBaseImportUserVO;
import com.anynote.note.api.model.dto.NoteKnowledgeBaseDTO;
import com.anynote.note.service.KnowledgeBaseService;
import com.anynote.system.api.model.bo.LoginUser;
import com.anynote.system.api.model.dto.KnowledgeBaseUserImportDTO;
import com.anynote.system.api.model.po.SysUser;
import com.anynote.system.api.model.vo.KnowledgeBaseUserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库服务
 * @author 称霸幼儿园
 */
@Service
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper, NoteKnowledgeBase>
        implements KnowledgeBaseService {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserKnowledgeBaseMapper userKnowledgeBaseMapper;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private RemoteFileService remoteFileService;

    @Override
    public Long createKnowledgeBase(KnowledgeBaseCreateParam createParam) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        Date date = new Date();
        NoteKnowledgeBase noteKnowledgeBase = NoteKnowledgeBase.builder()
                .id(createParam.getId())
                .name(createParam.getName())
                .cover(createParam.getCover())
                .type(createParam.getType())
                .status(0)
                .deleted(0)
                .build();
        noteKnowledgeBase.setCreateBy(loginUser.getSysUser().getId());
        noteKnowledgeBase.setCreateTime(date);
        noteKnowledgeBase.setUpdateBy(loginUser.getSysUser().getId());
        noteKnowledgeBase.setCreateTime(date);
        this.baseMapper.insert(noteKnowledgeBase);
        return noteKnowledgeBase.getId();
    }

    @Override
    public List<Long> getUsersKnowledgeBaseIds(Long userId) {
        LambdaQueryWrapper<UserKnowledgeBase> userKnowledgeBaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userKnowledgeBaseLambdaQueryWrapper
                .eq(UserKnowledgeBase::getUserId, userId)
                .le(UserKnowledgeBase::getPermissions, KnowledgeBasePermissions.READ.getValue())
                .select(UserKnowledgeBase::getKnowledgeBaseId);
        List<UserKnowledgeBase> userKnowledgeBaseList = userKnowledgeBaseMapper.selectList(userKnowledgeBaseLambdaQueryWrapper);
        return userKnowledgeBaseList.stream()
                .map(knowledgeBaseId ->
                        knowledgeBaseId.getKnowledgeBaseId())
                .collect(Collectors.toList());
    }

    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.READ, message = "没有获取该知识库权限")
    @Override
    public NoteKnowledgeBaseDTO getKnowledgeBaseById(KnowledgeBaseQueryParam queryParam) {
//        LoginUser loginUser = tokenUtil.getLoginUser();
//        KnowledgeBaseQueryParam queryParam = new KnowledgeBaseQueryParam();
//        queryParam.setId(id);
        NoteKnowledgeBaseDTO noteKnowledgeBaseDTO = this.baseMapper.selectKnowledgeBaseById(queryParam);
        if (StringUtils.isNull(noteKnowledgeBaseDTO)) {
            throw new UserParamException("获取知识库失败", ResCode.INVALID_USER_INPUT_NOT_FOUND);
        }
        noteKnowledgeBaseDTO.setPermissions(((Integer) queryParam.getParams()
                .get(KnowledgeBasePermissionsAspect.KNOWLEDGE_BASE_PERMISSIONS)));
        return noteKnowledgeBaseDTO;
    }
    @Override
    @DataScope
    public PageBean<NoteKnowledgeBaseDTO> getUserKnowledgeBases(Integer page, Integer pageSize) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        PageHelper.startPage(page, pageSize, "update_time desc");
        List<NoteKnowledgeBaseDTO> noteKnowledgeBaseDTOList = this.baseMapper.selectUserKnowledgeBaseList(KnowledgeBaseQueryParam.builder()
                .status(0)
                .build());
        PageInfo<NoteKnowledgeBaseDTO> pageInfo = new PageInfo<>(noteKnowledgeBaseDTOList);
        return PageBean.<NoteKnowledgeBaseDTO>builder()
                .current(page)
                .rows(noteKnowledgeBaseDTOList)
                .total(pageInfo.getTotal())
                .pages(pageInfo.getPages())
                .build();
    }

    @Override
    public List<Long> getAllKnowledgeBaseUserId(Long knowledgeBaseId) {
        LambdaQueryWrapper<UserKnowledgeBase> userKnowledgeBaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userKnowledgeBaseLambdaQueryWrapper
                .eq(UserKnowledgeBase::getKnowledgeBaseId, knowledgeBaseId)
                .select(UserKnowledgeBase::getUserId);
        List<UserKnowledgeBase> userIdList = userKnowledgeBaseMapper.selectList(userKnowledgeBaseLambdaQueryWrapper);
        return userIdList.stream().map(userKnowledgeBase -> {
            return userKnowledgeBase.getUserId();
        }).collect(Collectors.toList());
    }

    @Override
    public List<Long> getAllKnowledgeBaseManagerId(Long knowledgeBaseId) {
        LambdaQueryWrapper<UserKnowledgeBase> userKnowledgeBaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userKnowledgeBaseLambdaQueryWrapper
                .eq(UserKnowledgeBase::getKnowledgeBaseId, knowledgeBaseId)
                .eq(UserKnowledgeBase::getPermissions, KnowledgeBasePermissions.MANAGE.getValue())
                .select(UserKnowledgeBase::getUserId);
        List<UserKnowledgeBase> userIdList = userKnowledgeBaseMapper.selectList(userKnowledgeBaseLambdaQueryWrapper);
        return userIdList.stream().map(userKnowledgeBase -> userKnowledgeBase.getUserId()).collect(Collectors.toList());
    }

    @Override
    public List<Long> getAllMemberKnowledgeBaseUserId(Long knowledgeBaseId) {
        LambdaQueryWrapper<UserKnowledgeBase> userKnowledgeBaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userKnowledgeBaseLambdaQueryWrapper
                .eq(UserKnowledgeBase::getKnowledgeBaseId, knowledgeBaseId)
                .gt(UserKnowledgeBase::getPermissions, KnowledgeBasePermissions.MANAGE.getValue())
                .select(UserKnowledgeBase::getUserId);;
        List<UserKnowledgeBase> userIdList = userKnowledgeBaseMapper.selectList(userKnowledgeBaseLambdaQueryWrapper);
        return userIdList.stream().map(userKnowledgeBase -> userKnowledgeBase.getUserId()).collect(Collectors.toList());
    }

    @Override
    public PageBean<NoteKnowledgeBaseDTO> getUsersOrganizationKnowledgeBase(Integer page, Integer pageSize) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        PageHelper.startPage(page, pageSize,"update_time desc");
        List<NoteKnowledgeBaseDTO> noteKnowledgeBaseDTOList =
                this.selectOrganizationKnowledgeBasesByUserIdByPage(loginUser.getUserId());
        PageInfo<NoteKnowledgeBaseDTO> pageInfo = new PageInfo<>(noteKnowledgeBaseDTOList);
        PageBean pageBean = new PageBean();
        pageBean.setRows(noteKnowledgeBaseDTOList);
        pageBean.setTotal(pageInfo.getTotal());
        pageBean.setPages(pageInfo.getPages());
        return pageBean;
    }

    @PageValid
    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.READ, message = "没有权限获取知识库用户")
    @Override
    public PageBean<KnowledgeBaseUserVO> getKnowledgeBaseUsers(KnowledgeBaseUsersQueryParam queryParam) {
        ResData<PageBean<KnowledgeBaseUserVO>> resData = remoteUserService
                .getKnowledgeBaseUsers(queryParam.getId(), queryParam.getPage(), queryParam.getPageSize());

        if (StringUtils.isNull(resData) || StringUtils.isNull(resData.getData())) {
            throw new BusinessException("获取知识库用户失败，请联系管理员");
        }

        if (!ResData.SUCCESS.equals(resData.getCode())) {
            throw new BusinessException("获取知识库用户失败，请联系管理员");
        }
        return resData.getData();
    }

    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "没有权限导入用户")
    @Override
    public KnowledgeBaseImportUserVO importKnowledgeBaseUser(KnowledgeBaseImportUserParam importUserParam) {
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(importUserParam.getExcel().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("用户名单解析异常", ResCode.USER_UPLOAD_ERROR);
        }

        if (StringUtils.isNull(workbook)) {
            throw new BusinessException("用户名单解析异常", ResCode.USER_UPLOAD_ERROR);
        }

        LoginUser loginUser = tokenUtil.getLoginUser();

        Sheet sheet = workbook.getSheetAt(0);
        List<KnowledgeBaseImportUser> userList = new ArrayList<>();
        for (Row row : sheet) {
            if (0 == row.getRowNum()) {
                continue;
            }
            KnowledgeBaseImportUser importUser = new KnowledgeBaseImportUser();
            for (Cell cell : row) {
                if (0 == cell.getAddress().getColumn()) {
                    if (CellType.STRING == cell.getCellType()) {
                        importUser.setUsername(cell.getStringCellValue());
                    }
                    else if (CellType.NUMERIC == cell.getCellType()) {
                        importUser.setUsername(new BigDecimal(String.valueOf(cell.getNumericCellValue()))
                                .stripTrailingZeros().toPlainString());
                    }
                }
                else if (1 == cell.getAddress().getColumn()) {
                    if (CellType.STRING == cell.getCellType()) {
                        importUser.setNickname(cell.getStringCellValue());
                    }
                    else if (CellType.NUMERIC == cell.getCellType()) {
                        importUser.setNickname(new BigDecimal(String.valueOf(cell.getNumericCellValue()))
                                .stripTrailingZeros().toPlainString());
                    }
                }
                else if (2 == cell.getAddress().getColumn()) {
                    if (CellType.STRING == cell.getCellType()) {
                        importUser.setClassName(cell.getStringCellValue());
                    }
                    else if (CellType.NUMERIC == cell.getCellType()) {
                        importUser.setClassName(new BigDecimal(String.valueOf(cell.getNumericCellValue()))
                                .stripTrailingZeros().toPlainString());
                    }
                }
            }
            Date date = new Date();
            importUser.setCreateBy(loginUser.getSysUser().getId());
            importUser.setCreateTime(date);
            importUser.setUpdateBy(loginUser.getSysUser().getId());
            importUser.setUpdateTime(date);
            userList.add(importUser);
        }
        ResData<KnowledgeBaseUserImportDTO> userImportResData = remoteUserService.importKnowledgeBaseUser(KnowledgeBaseUserImportDTO.builder()
                .knowledgeBaseImportUserList(userList)
                .build());
        if (StringUtils.isNull(userImportResData) || StringUtils.isNull(userImportResData.getData())) {
            throw new BusinessException("导入名单失败");
        }

        if (!ResData.SUCCESS.equals(userImportResData.getCode())) {
            throw new BusinessException("导入名单失败");
        }

        List<KnowledgeBaseImportUser> resKnowledgeBaseUserList = userImportResData.getData().getKnowledgeBaseImportUserList();

        associateKnowledgeUserKnowledgeBase(resKnowledgeBaseUserList, importUserParam.getId());

        addKnowledgeBaseUserPassword(sheet, resKnowledgeBaseUserList);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("导入名单失败");
        }
//        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        MultipartFile multipartFile = MultipartFileUtil.toMultipartFile(bos.toByteArray(), "Members.xlsx");
        ResData<FileDTO> fileDTOResData = remoteFileService.uploadFile(multipartFile, FileConstants.KNOWLEDGE_BASE_PATH +
                StringUtils.format("/{}/", importUserParam.getId()) + FileConstants.MEMBER_EXCELS);
        if (StringUtils.isNull(fileDTOResData) || StringUtils.isNull(fileDTOResData.getData())) {
            throw new BusinessException("导入名单失败");
        }

        if (!ResData.SUCCESS.equals(fileDTOResData.getCode())) {
            throw new BusinessException("导入名单失败");
        }
        return KnowledgeBaseImportUserVO.builder()
                .excelUrl(fileDTOResData.getData().getUrl())
                .failCount(userImportResData.getData().getFailCount())
                .failUserNameList(userImportResData.getData().getFailUserNameList())
                .build();
    }

    private void addKnowledgeBaseUserPassword(Sheet sheet, List<KnowledgeBaseImportUser> knowledgeBaseImportUserList) {
        for (Row row : sheet) {
            if (0 == row.getRowNum()) {
                row.getCell(0).setCellValue("学号（用户名）");
                Cell cell = row.createCell(3);
                cell.setCellValue("密码");
                continue;
            }
            Cell cell = row.createCell(3);
            cell.setCellValue(knowledgeBaseImportUserList.get(row.getRowNum()-1).getPassword());
        }
    }

    private void associateKnowledgeUserKnowledgeBase(List<KnowledgeBaseImportUser> knowledgeBaseImportUserList, Long knowledgeBaseId) {
        knowledgeBaseImportUserList.stream().forEach(
                knowledgeBaseImportUser -> {
                    if (StringUtils.isNotNull(knowledgeBaseImportUser.getUserId())) {
                        this.baseMapper.insertUserKnowledgeBase(knowledgeBaseImportUser.getUserId(), knowledgeBaseId,
                                KnowledgeBasePermissions.EDIT.getValue());
                    }
                }
        );
    }

    @Override
    public List<NoteKnowledgeBaseDTO> selectOrganizationKnowledgeBasesByUserIdByPage(Long userId) {
        KnowledgeBaseQueryParam queryParam = new KnowledgeBaseQueryParam();
        queryParam.setStatus(0);
        return this.baseMapper.selectOrganizationKnowledgeBaseList(queryParam);
    }

    @Override
    public Integer getUserKnowledgeBasePermissions(Long userId, Long knowledgeBaseId) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        if (SysUser.isAdminX(loginUser.getSysUser().getRole())) {
            return 1;
        }
        return this.baseMapper.selectUserKnowledgeBasePermissions(userId, knowledgeBaseId);
    }

    /**
     * 获取知识库成员数量(不包含管理员)
     * @param queryParam
     * @return
     */
    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.READ, message = "没有权限查看知识库总人数")
    @Override
    public Long getKnowledgeBaseMemberCount(KnowledgeBaseQueryParam queryParam) {
        return this.baseMapper.selectKnowledgeBaseMembersCount(queryParam.getId());
    }

    @Override
    public Integer getUserKnowledgeBasePermissionsByNoteId(Long userId, Long noteId) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        if (SysUser.isAdminX(loginUser.getSysUser().getRole())) {
            return 1;
        }
        return this.baseMapper.selectUserKnowledgeBasePermissionsByNoteId(userId, noteId);
    }

    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "没有权限更新知识库信息")
    @Override
    public String updateKnowledgeBase(KnowledgeBaseUpdateParam updateParam) {
        NoteKnowledgeBase knowledgeBase = NoteKnowledgeBase.builder()
                .id(updateParam.getId())
                .name(updateParam.getName())
                .detail(updateParam.getDetail())
                .cover(updateParam.getCover())
                .build();
        Integer count = this.baseMapper.updateById(knowledgeBase);
        if (1 != count) {
            throw new BusinessException("更新知识库失败，请联系管理员");
        }
        return Constants.SUCCESS_RES;
    }


    @Override
    public PageBean<NoteKnowledgeBaseDTO> getManagerKnowledgeBaseList(Integer page, Integer pageSize) {

        return null;
    }
}

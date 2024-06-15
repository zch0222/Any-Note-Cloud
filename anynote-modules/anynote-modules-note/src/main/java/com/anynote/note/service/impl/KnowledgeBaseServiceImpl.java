package com.anynote.note.service.impl;

import com.anynote.common.datascope.annotation.DataScope;
import com.anynote.common.security.token.TokenUtil;
import com.anynote.core.constant.Constants;
import com.anynote.core.constant.FileConstants;
import com.anynote.core.constant.HuaweiOBSConstants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.exception.user.UserParamException;
import com.anynote.core.utils.*;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.RemoteFileService;
import com.anynote.file.api.enums.FileSources;
import com.anynote.file.api.model.bo.FileDTO;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.file.api.model.dto.CreateHuaweiOBSTemporarySignatureDTO;
import com.anynote.file.api.model.po.FilePO;
import com.anynote.note.api.model.po.NoteTask;
import com.anynote.note.api.model.po.UserKnowledgeBase;
import com.anynote.note.api.model.po.UserNoteTask;
import com.anynote.note.enums.NoteTaskPermissions;
import com.anynote.note.mapper.UserKnowledgeBaseMapper;
import com.anynote.note.model.bo.*;
import com.anynote.note.model.dto.CompleteKnowledgeBaseUploadDTO;
import com.anynote.note.model.dto.CreateKnowledgeBaeDTO;
import com.anynote.note.model.dto.KnowledgeBaseCoverUploadTempLinkDTO;
import com.anynote.note.model.vo.CreateKnowledgeBaseVO;
import com.anynote.note.service.NoteTaskService;
import com.anynote.note.validate.annotation.PageValid;
import com.anynote.system.api.RemoteUserService;
import com.anynote.system.api.enums.KnowledgeBaseUserImportErrorType;
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
import com.anynote.system.api.model.dto.ImportFailUser;
import com.anynote.system.api.model.dto.KnowledgeBaseUserImportDTO;
import com.anynote.system.api.model.po.SysUser;
import com.anynote.system.api.model.vo.KnowledgeBaseUserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
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

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private RemoteFileService remoteFileService;

    @Resource
    @Lazy
    private NoteTaskService noteTaskService;

//    @Override
//    public Long createKnowledgeBase(KnowledgeBaseCreateParam createParam) {
//        LoginUser loginUser = tokenUtil.getLoginUser();
//        Date date = new Date();
//        NoteKnowledgeBase noteKnowledgeBase = NoteKnowledgeBase.builder()
//                .id(createParam.getId())
//                .name(createParam.getName())
//                .cover(createParam.getCover())
//                .type(createParam.getType())
//                .status(0)
//                .deleted(0)
//                .build();
//        noteKnowledgeBase.setCreateBy(loginUser.getSysUser().getId());
//        noteKnowledgeBase.setCreateTime(date);
//        noteKnowledgeBase.setUpdateBy(loginUser.getSysUser().getId());
//        noteKnowledgeBase.setCreateTime(date);
//        this.baseMapper.insert(noteKnowledgeBase);
//        return noteKnowledgeBase.getId();
//    }

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
    public PageBean<NoteKnowledgeBaseDTO> getUserKnowledgeBases(Integer page, Integer pageSize, Integer permissions) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        KnowledgeBaseQueryParam queryParam = KnowledgeBaseQueryParam.builder()
                .permissions(permissions)
                .status(0)
                .build();
        PageHelper.startPage(page, pageSize, "update_time desc");
        List<NoteKnowledgeBaseDTO> noteKnowledgeBaseDTOList = this.baseMapper.selectUserKnowledgeBaseList(queryParam);
        PageInfo<NoteKnowledgeBaseDTO> pageInfo = new PageInfo<>(noteKnowledgeBaseDTOList);
        return PageBean.<NoteKnowledgeBaseDTO>builder()
                .current(page)
                .rows(noteKnowledgeBaseDTOList)
                .total(pageInfo.getTotal())
                .pages(pageInfo.getPages())
                .build();
    }

    @Override
    public FileDTO uploadKnowledgeBaseCover(MultipartFile image, String uploadId) {
        ResData<FilePO> fileDTOResData = remoteFileService.uploadFile(image, FileConstants.KNOWLEDGE_BASE_COVER,
                tokenUtil.getUserId(),uploadId, FileSources.KNOWLEDGE_BASE_COVER.getValue());
        FilePO filePO = RemoteResDataUtil.getResData(fileDTOResData, "封面上传失败");
        return FileDTO.builder()
                .fileId(filePO.getId())
                .originalFileName(filePO.getOriginalFileName())
                .fileName(filePO.getFileName())
                .hash(filePO.getHash())
                .url(filePO.getUrl())
                .source(filePO.getSource())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CreateKnowledgeBaseVO createKnowledgeBase(CreateKnowledgeBaeDTO createKnowledgeBaeDTO) {
        LoginUser loginUser = tokenUtil.getLoginUser();
        Date date = new Date();
        NoteKnowledgeBase noteKnowledgeBase = NoteKnowledgeBase.builder()
                .name(createKnowledgeBaeDTO.getName())
                .cover(createKnowledgeBaeDTO.getCover())
                .type(createKnowledgeBaeDTO.getType())
                .detail(createKnowledgeBaeDTO.getDetail())
                .status(0)
                .deleted(0)
                .build();
        noteKnowledgeBase.setCreateBy(loginUser.getSysUser().getId());
        noteKnowledgeBase.setCreateTime(date);
        noteKnowledgeBase.setUpdateBy(loginUser.getSysUser().getId());
        noteKnowledgeBase.setUpdateTime(date);
        int count = this.baseMapper.insert(noteKnowledgeBase);
        if (count != 1) {
            throw new BusinessException("创建失败");
        }
        userKnowledgeBaseMapper.insert(UserKnowledgeBase.builder()
                        .knowledgeBaseId(noteKnowledgeBase.getId())
                        .userId(loginUser.getSysUser().getId())
                        .permissions(1)
                .build());
        return CreateKnowledgeBaseVO.builder()
                .id(noteKnowledgeBase.getId())
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
                .getKnowledgeBaseUsers(queryParam.getId(), queryParam.getPage(), queryParam.getPageSize(), queryParam.getUsername());

        if (StringUtils.isNull(resData) || StringUtils.isNull(resData.getData())) {
            throw new BusinessException("获取知识库用户失败，请联系管理员");
        }

        if (!ResData.SUCCESS.equals(resData.getCode())) {
            throw new BusinessException("获取知识库用户失败，请联系管理员");
        }
        return resData.getData();
    }

    @RequiresKnowledgeBasePermissions(value = KnowledgeBasePermissions.MANAGE, message = "没有权限执行操作")
    @Override
    public String removeKnowledgeBaseUser(KnowledgeBaseUsersDeleteParam deleteParam) {
        if (deleteParam.getUserId().equals(tokenUtil.getUserId())) {
            throw new BusinessException("您不能移除自己");
        }
        LambdaQueryWrapper<UserKnowledgeBase> userKnowledgeBaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userKnowledgeBaseLambdaQueryWrapper
                .eq(UserKnowledgeBase::getKnowledgeBaseId, deleteParam.getKnowledgeBaseId())
                .eq(UserKnowledgeBase::getUserId, deleteParam.getUserId());
        int res = userKnowledgeBaseMapper.delete(userKnowledgeBaseLambdaQueryWrapper);
        if (1 == res) {
            return Constants.SUCCESS_RES;
        }
        throw new BusinessException("移除失败，请联系管理员");
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

        KnowledgeBaseUserImportDTO knowledgeBaseUserImportDTO = userImportResData.getData();

        associateKnowledgeUserKnowledgeBase(knowledgeBaseUserImportDTO, importUserParam.getId());

        addKnowledgeBaseUserPassword(sheet, knowledgeBaseUserImportDTO.getKnowledgeBaseImportUserList());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("导入名单失败");
        }
//        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        MultipartFile multipartFile = MultipartFileUtil.toMultipartFile(bos.toByteArray(), "Members.xls");
        ResData<FilePO> fileDTOResData = remoteFileService.uploadFile(multipartFile, FileConstants.KNOWLEDGE_BASE_PATH +
                StringUtils.format("/{}/", importUserParam.getId()) + FileConstants.MEMBER_EXCELS, loginUser.getSysUser().getId(),
                UUID.randomUUID().toString().replace("-", ""), FileSources.KNOWLEDGE_BASE_MEMBER_EXCEL.getValue());
        if (StringUtils.isNull(fileDTOResData) || StringUtils.isNull(fileDTOResData.getData())) {
            throw new BusinessException("导入名单失败");
        }

        if (!ResData.SUCCESS.equals(fileDTOResData.getCode())) {
            throw new BusinessException("导入名单失败");
        }
        return KnowledgeBaseImportUserVO.builder()
                .excelUrl(fileDTOResData.getData().getUrl())
                .failCount(knowledgeBaseUserImportDTO.getFailUserList().size())
                .failUserList(knowledgeBaseUserImportDTO.getFailUserList())
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

    private void associateKnowledgeUserKnowledgeBase(KnowledgeBaseUserImportDTO knowledgeBaseUserImportDTO, Long knowledgeBaseId) {
        List<KnowledgeBaseImportUser> knowledgeBaseImportUserList = knowledgeBaseUserImportDTO.getKnowledgeBaseImportUserList();
        List<ImportFailUser> failUserList = knowledgeBaseUserImportDTO.getFailUserList();
        knowledgeBaseImportUserList.forEach(
                knowledgeBaseImportUser -> {
                    if (StringUtils.isNull(knowledgeBaseImportUser.getUserId())) {
                        return;
                    }

                    if (StringUtils.isNull(this.getUserKnowledgeBase(knowledgeBaseImportUser.getUserId(), knowledgeBaseId))) {
//                        ((KnowledgeBaseService)AopContext.currentProxy()).addUserToKnowledgeBase(UserKnowledgeBase.builder()
//                                .userId(knowledgeBaseImportUser.getUserId())
//                                .knowledgeBaseId(knowledgeBaseId)
//                                .permissions(KnowledgeBasePermissions.EDIT.getValue())
//                                .build());
                        AopUtil.getCurrentProxy(this.getClass()).addUserToKnowledgeBase(UserKnowledgeBase.builder()
                                .userId(knowledgeBaseImportUser.getUserId())
                                .knowledgeBaseId(knowledgeBaseId)
                                .permissions(KnowledgeBasePermissions.EDIT.getValue())
                                .build());
//                        this.baseMapper.insertUserKnowledgeBase(knowledgeBaseImportUser.getUserId(), knowledgeBaseId,
//                                KnowledgeBasePermissions.EDIT.getValue());
                        //

                    }
                    else {
                        failUserList.add(ImportFailUser.builder()
                                        .username(knowledgeBaseImportUser.getUsername())
                                .reason(KnowledgeBaseUserImportErrorType.USER_EXISTS_IN_KNOWLEDGE_BASE.getValue()).build());
                        knowledgeBaseImportUser.setPassword(KnowledgeBaseUserImportErrorType.USER_EXISTS_IN_KNOWLEDGE_BASE.getValue());
                    }
                }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void addUserToKnowledgeBase(UserKnowledgeBase userKnowledgeBase) {
        userKnowledgeBaseMapper.insert(userKnowledgeBase);
        List<NoteTask> noteTasks = noteTaskService.getNoteTasksByKnowledgeBaseId(userKnowledgeBase.getKnowledgeBaseId());
        for (NoteTask noteTask : noteTasks) {
            try {
                noteTaskService.insertUserNoteTask(UserNoteTask.builder()
                        .noteTaskId(noteTask.getId())
                        .userId(userKnowledgeBase.getUserId())
                        .permissions(NoteTaskPermissions.SUBMIT.getValue())
                        .status(0)
                        .build());
            } catch (Exception e) {
                log.error("管理任务失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取用户知识库
     * @param userId
     * @param knowledgeBaseId
     * @return
     */
    public UserKnowledgeBase getUserKnowledgeBase(Long userId, Long knowledgeBaseId) {
        LambdaQueryWrapper<UserKnowledgeBase> userKnowledgeBaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userKnowledgeBaseLambdaQueryWrapper
                .eq(UserKnowledgeBase::getUserId, userId)
                .eq(UserKnowledgeBase::getKnowledgeBaseId, knowledgeBaseId);
        return userKnowledgeBaseMapper.selectOne(userKnowledgeBaseLambdaQueryWrapper);
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
    public PageBean<NoteKnowledgeBaseDTO> getManagerKnowledgeBaseList(KnowledgeBaseQueryParam queryParam) {
        PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize(), "create_time DESC");
        List<NoteKnowledgeBaseDTO> noteKnowledgeBaseDTOList = this.baseMapper.selectKnowledgeBaseList(queryParam);
        PageInfo<NoteKnowledgeBaseDTO> pageInfo = new PageInfo<>(noteKnowledgeBaseDTOList);
        return PageBean.<NoteKnowledgeBaseDTO>builder()
                .current(queryParam.getPage())
                .pages(pageInfo.getPages())
                .total(pageInfo.getTotal())
                .rows(noteKnowledgeBaseDTOList)
                .build();
    }

    @Override
    public HuaweiOBSTemporarySignature createCoverUploadTempSignature(KnowledgeBaseCoverUploadTempLinkDTO uploadTempLinkDTO) {
        return RemoteResDataUtil.getResData(remoteFileService.createHuaweiOBSTemporarySignature(CreateHuaweiOBSTemporarySignatureDTO.builder()
                        .contentType(uploadTempLinkDTO.getContentType())
                        .expireSeconds(HuaweiOBSConstants.KNOWLEDGE_BASE_COVER_TEMPORARY_SIGNATURE_EXPIRE_SECONDS)
                        .fileName(uploadTempLinkDTO.getFileName())
                        .path(FileConstants.KNOWLEDGE_BASE_COVER)
                        .source(FileSources.KNOWLEDGE_BASE_COVER.getValue()).build()), "图片上传失败");
    }

    @Override
    public String completeCoverUpload(CompleteKnowledgeBaseUploadDTO completeKnowledgeBaseUploadDTO) {
        FilePO filePO = RemoteResDataUtil.getResData(remoteFileService
                .completeHuaweiOBSUpload(completeKnowledgeBaseUploadDTO), "上传异常，请联系管理员");
        return Constants.SUCCESS_RES;
    }

    @Override
    public List<Long> getKnowledgeBaseUserIds(Long knowledgeBaseId) {
        LambdaQueryWrapper<UserKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .eq(UserKnowledgeBase::getKnowledgeBaseId, knowledgeBaseId);
        List<UserKnowledgeBase> userKnowledgeBaseList = userKnowledgeBaseMapper.selectList(queryWrapper);
        return userKnowledgeBaseList.stream().map(UserKnowledgeBase::getUserId).collect(Collectors.toList());
    }

    @Override
    public NoteKnowledgeBaseDTO getKnowledgeBaseById(Long id) {
        KnowledgeBaseQueryParam queryParam = KnowledgeBaseQueryParam.builder().id(id).build();
        queryParam.setParams(new HashMap<>(0));
        return this.baseMapper.selectKnowledgeBaseById(queryParam);
    }
}

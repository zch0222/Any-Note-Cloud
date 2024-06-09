package com.anynote.note.service;

import com.anynote.core.web.model.bo.PageBean;
import com.anynote.file.api.model.bo.FileDTO;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.note.api.model.po.NoteKnowledgeBase;
import com.anynote.note.api.model.po.UserKnowledgeBase;
import com.anynote.note.model.bo.*;
import com.anynote.note.model.dto.CompleteKnowledgeBaseUploadDTO;
import com.anynote.note.model.dto.CreateKnowledgeBaeDTO;
import com.anynote.note.model.dto.KnowledgeBaseCoverUploadTempLinkDTO;
import com.anynote.note.model.dto.KnowledgeBaseImportUserVO;
import com.anynote.note.api.model.dto.NoteKnowledgeBaseDTO;
import com.anynote.note.model.vo.CreateKnowledgeBaseVO;
import com.anynote.note.model.vo.UploadKnowledgeBaeCoverVO;
import com.anynote.system.api.model.vo.KnowledgeBaseUserVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库服务
 * @author 称霸幼儿园
 */
public interface KnowledgeBaseService extends IService<NoteKnowledgeBase> {

 //   public Long createKnowledgeBase(KnowledgeBaseCreateParam createParam);

    public NoteKnowledgeBaseDTO getKnowledgeBaseById(KnowledgeBaseQueryParam queryParam);

    public PageBean<NoteKnowledgeBaseDTO> getUsersOrganizationKnowledgeBase(Integer page, Integer pageSize);

    /**
     * 上传知识库封面
     * @param image 封面
     * @return
     */
    public FileDTO uploadKnowledgeBaseCover(MultipartFile image, String uploadId);

    /**
     * 创建知识库
     * @param createKnowledgeBaeDTO 创建知识库传输类
     * @return
     */
    public CreateKnowledgeBaseVO createKnowledgeBase(CreateKnowledgeBaeDTO createKnowledgeBaeDTO);

    /**
     * 获取用户知识库
     * @param page
     * @param pageSize
     * @return
     */
    public PageBean<NoteKnowledgeBaseDTO> getUserKnowledgeBases(Integer page, Integer pageSize, Integer permissions);

    /**
     * 获取用户所有知识库的id
     * @param userId 用户id
     * @return
     */
    public List<Long> getUsersKnowledgeBaseIds(Long userId);

    /**
     * 获取知识库中所有用户的id
     * @param knowledgeBaseId
     * @return
     */
    public List<Long> getAllKnowledgeBaseUserId(Long knowledgeBaseId);

    public List<Long> getAllKnowledgeBaseManagerId(Long knowledgeBaseId);

    public List<Long> getAllMemberKnowledgeBaseUserId(Long knowledgeBaseId);

    public PageBean<KnowledgeBaseUserVO> getKnowledgeBaseUsers(KnowledgeBaseUsersQueryParam queryParam);

    public String removeKnowledgeBaseUser(KnowledgeBaseUsersDeleteParam deleteParam);

    public List<NoteKnowledgeBaseDTO> selectOrganizationKnowledgeBasesByUserIdByPage(Long userId);

    public Integer getUserKnowledgeBasePermissions(Long userId, Long knowledgeBaseId);

    public Integer getUserKnowledgeBasePermissionsByNoteId(Long userId, Long noteId);

    public Long getKnowledgeBaseMemberCount(KnowledgeBaseQueryParam queryParam);

    public KnowledgeBaseImportUserVO importKnowledgeBaseUser(KnowledgeBaseImportUserParam importUserParam);


    public String updateKnowledgeBase(KnowledgeBaseUpdateParam updateParam);

    public HuaweiOBSTemporarySignature createCoverUploadTempSignature(KnowledgeBaseCoverUploadTempLinkDTO uploadTempLinkDTO);

    public String completeCoverUpload(CompleteKnowledgeBaseUploadDTO completeKnowledgeBaseUploadDTO);

    public UserKnowledgeBase getUserKnowledgeBase(Long userId, Long knowledgeBaseId);

    public void addUserToKnowledgeBase(UserKnowledgeBase userKnowledgeBase);


    /**
     * 超级管理员获取知识库列表
     * @param page
     * @param pageSize
     * @return
     */
    public PageBean<NoteKnowledgeBaseDTO> getManagerKnowledgeBaseList(KnowledgeBaseQueryParam queryParam);

    public List<Long> getKnowledgeBaseUserIds(Long knowledgeBaseId);
}

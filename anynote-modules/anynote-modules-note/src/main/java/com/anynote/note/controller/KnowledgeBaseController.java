package com.anynote.note.controller;

import com.anynote.common.datascope.annotation.DataScope;
import com.anynote.common.security.annotation.InnerAuth;
import com.anynote.core.constant.ErrorMessageConstants;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.validation.annotation.Upload;
import com.anynote.core.validation.annotation.Url;
import com.anynote.core.validation.enums.FileType;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.model.bo.FileDTO;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.note.model.bo.KnowledgeBaseQueryParam;
import com.anynote.note.model.bo.KnowledgeBaseUpdateParam;
import com.anynote.note.model.bo.KnowledgeBaseUsersDeleteParam;
import com.anynote.note.model.bo.KnowledgeBaseUsersQueryParam;
import com.anynote.note.model.dto.CompleteKnowledgeBaseUploadDTO;
import com.anynote.note.model.dto.CreateKnowledgeBaeDTO;
import com.anynote.note.model.dto.KnowledgeBaseCoverUploadTempLinkDTO;
import com.anynote.note.model.dto.KnowledgeBaseUpdateDTO;
import com.anynote.note.api.model.dto.NoteKnowledgeBaseDTO;
import com.anynote.note.model.vo.CreateKnowledgeBaseVO;
import com.anynote.note.service.KnowledgeBaseService;
import com.anynote.system.api.model.vo.KnowledgeBaseUserVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 称霸幼儿园
 */
@RequestMapping("/bases")
@RestController
@Validated
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @DataScope
    @GetMapping("organizations")
    public ResData<PageBean<NoteKnowledgeBaseDTO>> getUsersOrganizationKnowledgeBases(@NotNull(message = "页码不能为空") Integer page,
                                                                                      @NotNull(message = "页面容量不能为空") Integer pageSize) {
        return ResData.success(knowledgeBaseService
                .getUsersOrganizationKnowledgeBase(page, pageSize));
    }

    /**
     * 超级管理员获取知识库列表
     * @param page
     * @param pageSize
     * @return
     */
    @InnerAuth
    @GetMapping("managerList")
    public ResData<PageBean<NoteKnowledgeBaseDTO>> getManagerKnowledgeBases(@NotNull(message = "页码不能为空") Integer page,
                                                                            @NotNull(message = "页面容量不能为空") Integer pageSize,
                                                                            Integer type,
                                                                            Integer status, Long organizationId) {
        return ResUtil.success(knowledgeBaseService.getManagerKnowledgeBaseList(KnowledgeBaseQueryParam.builder()
                        .page(page)
                        .pageSize(pageSize)
                        .type(type)
                        .status(status)
                        .organizationId(organizationId)
                .build()));
    }

    @Upload(value = FileType.IMAGE, max = 10)
    @PostMapping("covers")
    public ResData<FileDTO> uploadKnowledgeBaseCover(@NotNull(message = "图片不能为空") @RequestParam("image") MultipartFile image,
                                                     @NotNull(message = "uploadId不能为空") @RequestParam("uploadId") String uploadId) {
        return ResUtil.success(knowledgeBaseService.uploadKnowledgeBaseCover(image, uploadId));
    }

    @PostMapping("covers/img")
    public ResData<HuaweiOBSTemporarySignature> coverUploadTempLink(@Validated @RequestBody
                                                                        KnowledgeBaseCoverUploadTempLinkDTO uploadTempLinkDTO) {
        return ResUtil.success(knowledgeBaseService.createCoverUploadTempSignature(uploadTempLinkDTO));
    }

    @PutMapping("covers/img")
    public ResData<String> completeCoverUpload(@Validated @RequestBody CompleteKnowledgeBaseUploadDTO completeKnowledgeBaseUploadDTO) {
        return ResUtil.success(knowledgeBaseService.completeCoverUpload(completeKnowledgeBaseUploadDTO));
    }

    @Url(value = "cover", param = "createKnowledgeBaeDTO")
    @PostMapping
    public ResData<CreateKnowledgeBaseVO> createKnowledgeBase(@Validated @RequestBody CreateKnowledgeBaeDTO createKnowledgeBaeDTO) {
        return ResUtil.success(knowledgeBaseService.createKnowledgeBase(createKnowledgeBaeDTO));
    }


    @GetMapping
    public ResData<PageBean<NoteKnowledgeBaseDTO>> getUsersKnowledgeBases(@NotNull(message = "页码不能为空") Integer page,
                                                                          @NotNull(message = "页面容量不能为空") Integer pageSize,
                                                                          @NotNull(message = ErrorMessageConstants.QUERY_PARAM_ERROR) Integer permissions) {
        return ResUtil.success(knowledgeBaseService.getUserKnowledgeBases(page, pageSize, permissions));
    }

    @GetMapping("users")
    public ResData<PageBean<KnowledgeBaseUserVO>> getKnowledgeBaseUsers(@NotNull(message = "知识库id不能为空") Long knowledgeBaseId,
                                                                        @NotNull(message = "页码不能为空") Integer page,
                                                                        @NotNull(message = "页面容量不能为空") Integer pageSize,
                                                                        String username) {
        KnowledgeBaseUsersQueryParam queryParam = new KnowledgeBaseUsersQueryParam();
        queryParam.setId(knowledgeBaseId);
        queryParam.setPage(page);
        queryParam.setPageSize(pageSize);
        queryParam.setUsername(username);
        return ResUtil.success(knowledgeBaseService.getKnowledgeBaseUsers(queryParam));
    }

    /**
     * 获取知识库用户id列表
     * @param knowledgeBaseId
     * @return
     */
    @InnerAuth
    @GetMapping("users/ids/{knowledgeBaseId}")
    public ResData<List<Long>> getKnowledgeBaseUserIds(@NotNull(message = "知识库id不能为空") @PathVariable("knowledgeBaseId") Long knowledgeBaseId) {
        return ResUtil.success(knowledgeBaseService.getKnowledgeBaseUserIds(knowledgeBaseId));
    }

    @DeleteMapping("users")
    public ResData<String> removeKnowledgeBaseUser(@NotNull(message = "用户ID不能为空") Long userId,
                                                   @NotNull(message = "知识库ID不能为空") Long knowledgeBaseId) {
        KnowledgeBaseUsersDeleteParam knowledgeBaseUsersDeleteParam = new KnowledgeBaseUsersDeleteParam();
        knowledgeBaseUsersDeleteParam.setUserId(userId);
        knowledgeBaseUsersDeleteParam.setKnowledgeBaseId(knowledgeBaseId);
        return ResUtil.success(knowledgeBaseService.removeKnowledgeBaseUser(knowledgeBaseUsersDeleteParam));
    }

//    @PostMapping
//    public ResData<Long> createDataBase(@Validated @RequestBody KnowledgeBaseCreateDTO knowledgeBaseCreateDTO) {
//        KnowledgeBaseCreateParam knowledgeBaseCreateParam =
//                new KnowledgeBaseCreateParam(knowledgeBaseCreateDTO);
//        return ResUtil.success(knowledgeBaseService.createKnowledgeBase(knowledgeBaseCreateParam));
//    }

    @DataScope(userAlias = "sys_user",
            organizationAlias = "sys_organization")
    @GetMapping("{id}")
    public ResData<NoteKnowledgeBaseDTO> getKnowledgeBaseById(@NotNull(message = "知识库id不能为空") @PathVariable Long id) {
        KnowledgeBaseQueryParam queryParam = KnowledgeBaseQueryParam.builder()
                .id(id)
                .build();
        return ResData.success(knowledgeBaseService.getKnowledgeBaseById(queryParam));
    }

    @Url(value = "cover", param = "knowledgeBaseUpdateDTO")
    @PutMapping("{id}")
    public ResData<String> updateKnowledgeBase(
            @PathVariable("id") Long id,
            @RequestBody KnowledgeBaseUpdateDTO knowledgeBaseUpdateDTO) {
        knowledgeBaseUpdateDTO.setKnowledgeBaseId(id);
        KnowledgeBaseUpdateParam updateParam =
                new KnowledgeBaseUpdateParam(knowledgeBaseUpdateDTO);
        return ResData.success(knowledgeBaseService.updateKnowledgeBase(updateParam));
    }

}

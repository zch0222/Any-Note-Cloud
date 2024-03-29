package com.anynote.note.controller;

import com.anynote.common.datascope.annotation.DataScope;
import com.anynote.common.elasticsearch.model.EsNoteIndex;
import com.anynote.common.elasticsearch.model.bo.SearchPageBean;
import com.anynote.core.constant.ErrorMessageConstants;
import com.anynote.core.exception.user.UserParamException;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.PageBean;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.file.api.model.dto.CompleteUploadDTO;
import com.anynote.note.api.model.po.Note;
import com.anynote.note.api.model.po.NoteOperationLog;
import com.anynote.note.datascope.annotation.RequiresNotePermissions;
import com.anynote.note.enums.NotePermissions;
import com.anynote.note.mapper.NoteHistoryMapper;
import com.anynote.note.mapper.NoteOperationLogMapper;
import com.anynote.note.model.bo.*;
import com.anynote.note.model.dto.*;
import com.anynote.note.model.vo.NoteHistoryListItemVO;
import com.anynote.note.model.vo.NoteHistoryVO;
import com.anynote.note.service.NoteHistoryService;
import com.anynote.note.service.NoteOperationLogService;
import com.anynote.note.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 * @author 称霸幼儿园
 */
@RestController
@RequestMapping("/notes")
@Validated
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Resource
    private NoteHistoryService noteHistoryService;

//    @Resource
//    private NoteOperationLogMapper noteOperationLogMapper;
    @Resource
    private NoteOperationLogService noteOperationLogService;

    @GetMapping()
    public ResData<PageBean<Note>> getNoteList(Integer page, Integer pageSize, Long knowledgeBaseId) {
        return null;
    }


    /**
     * 获取最近更新的笔记 (未完成)
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("list")
    public ResData<PageBean<Note>> getNoteInfoList(@NotNull(message = ErrorMessageConstants.PAGE_NULL) Integer page,
                                         @NotNull(message = ErrorMessageConstants.PAGE_SIZE_NULL) Integer pageSize) {
        return null;
    }

    @DataScope
    @PostMapping("bases/{baseId}")
    public ResData<PageBean<Note>> getNoteInfosByKnowledgeBaseId(@NotNull(message = "知识库id不能为空")
                                                                 @PathVariable Long baseId,
                                                                 @RequestBody NoteQueryParam queryParam) {
        if (StringUtils.isNull(queryParam.getPage())) {
            throw new UserParamException("页码不能为空", ResCode.USER_REQUEST_PARAM_ERROR);
        }
        if (StringUtils.isNull(queryParam.getPageSize())) {
            throw new UserParamException("页面大小不能为空", ResCode.USER_REQUEST_PARAM_ERROR);
        }
        queryParam.setKnowledgeBaseId(baseId);
        return ResUtil.success(noteService.getNotesByKnowledgeBaseId(queryParam));
    }

    @GetMapping("{noteId}")
    public ResData<Note> getNoteById(@NotNull(message = "笔记id不能为空")
                                     @PathVariable Long noteId) {
        NoteQueryParam queryParam = NoteQueryParam.builder()
                .id(noteId)
                .build();
        return ResUtil.success(noteService.getNoteById(queryParam));
    }

    @DeleteMapping("{noteId}")
    public ResData<String> deleteNote(@NotNull(message = "笔记id不能为空") @PathVariable Long noteId) {
        NoteDeleteParam noteDeleteParam = new NoteDeleteParam();
        noteDeleteParam.setId(noteId);
        return ResUtil.success(noteService.deleteNote(noteDeleteParam));
    }

    /**
     * 新建笔记
     * @param noteCreateDTO
     * @return
     */
    @PostMapping()
    public ResData<Long> createNote(@Validated @RequestBody NoteCreateDTO noteCreateDTO) {
        NoteCreateParam createParam = new NoteCreateParam(noteCreateDTO);
        return ResData.success(noteService.createNote(createParam));
    }


    /**
     * 更新笔记内容
     * @param note
     * @return
     */
    @PatchMapping("{noteId}")
    public ResData<String> editNote(@NotNull(message = "笔记id不能为空") @PathVariable Long noteId,
                                    @RequestBody NoteEditDTO noteEditDTO) {
        noteEditDTO.setNoteId(noteId);
        return ResUtil.success(noteService.editNote(new NoteUpdateParam(noteEditDTO)));
    }

    @PostMapping("images")
    public ResData<MarkdownImage> uploadNoteImage(@RequestParam("image") @NotNull(message = "图片文件不能为空") MultipartFile image,
                                                  @RequestParam("noteId") @NotNull(message = "笔记id不能为空") Long noteId,
                                                  @RequestParam(value = "uploadId", required = false) String uploadId) {
        NoteImageUploadParam imageUploadParam = new NoteImageUploadParam();
        imageUploadParam.setId(noteId);
        imageUploadParam.setImage(image);
        imageUploadParam.setUploadId(uploadId);
        return ResUtil.success(noteService.uploadNoteImage(imageUploadParam));
    }

    @PostMapping("img")
    public ResData<HuaweiOBSTemporarySignature> imageUploadTempLink(@Validated @RequestBody NoteImageUploadTempLinkDTO noteImageUploadTempLinkDTO) {
        return ResUtil.success(noteService.getImageUploadTempSignature(NoteImageUploadSignatureCreateParam
                .NoteImageUploadSignatureCreateParamBuilder()
                        .fileName(noteImageUploadTempLinkDTO.getFileName())
                        .contentType(noteImageUploadTempLinkDTO.getContentType())
                        .noteId(noteImageUploadTempLinkDTO.getNoteId()).build()));
    }

    @PutMapping("img")
    public ResData<String> completeNoteImageUpload(@Validated @RequestBody CompleteNoteImageUploadDTO completeUploadDTO) {
        return ResUtil.success(noteService.completeNoteImageUpload(NoteImageCompleteParam.NoteImageCompleteParamBuilder()
                        .noteId(completeUploadDTO.getNoteId())
                        .hash(completeUploadDTO.getHash())
                        .uploadId(completeUploadDTO.getUploadId()).build()));
    }


    @GetMapping("search")
    public ResData<SearchPageBean<EsNoteIndex>> searchNote(@Valid NoteSearchDTO noteSearchDTO) {
        return ResUtil.success(noteService.searchNote(noteSearchDTO));
    }

    /**
     * 笔记历史列表
     * @param noteId 笔记id
     * @param page 笔记页码
     * @param pageSize
     * @return
     */
    @GetMapping("historyList")
    public ResData<PageBean<NoteHistoryListItemVO>> getHistoryList(Long noteId, Integer page, Integer pageSize) {
        return ResUtil.success(noteHistoryService.getNoteHistoryListItemVOList(NoteHistoryListItemQueryParam.NoteHistoryListItemQueryParamBuilder()
                .noteId(noteId)
                .page(page)
                .pageSize(pageSize)
                .build()));
    }

    @GetMapping("history")
    public ResData<NoteHistoryVO> getNoteHistory(Long operationId) {
        NoteOperationLog noteOperationLog = noteOperationLogService.getBaseMapper().selectById(operationId);
        return ResUtil.success(noteHistoryService.getNoteHistory(NoteHistoryQueryParam.NoteHistoryQueryParamBuilder()
                        .noteId(noteOperationLog.getNoteId())
                        .operationId(operationId)
                .build()));
    }







}

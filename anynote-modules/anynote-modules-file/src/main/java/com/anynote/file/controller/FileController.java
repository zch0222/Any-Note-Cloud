package com.anynote.file.controller;

import com.anynote.common.security.annotation.InnerAuth;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.model.bo.*;
import com.anynote.file.api.model.dto.CompleteUploadDTO;
import com.anynote.file.api.model.dto.CreateHuaweiOBSTemporarySignatureDTO;
import com.anynote.file.api.model.po.FilePO;
import com.anynote.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author 称霸幼儿园
 */
@RestController
@RequestMapping("")
@Validated
public class FileController {

    @Autowired
    private FileService fileService;

    @InnerAuth
    @PostMapping
    public ResData<FilePO> uploadFile(@RequestParam("file") @NotNull(message = "文件不能为空") CommonsMultipartFile file,
                                      @RequestParam("path") @NotNull(message = "文件路径不能为空") String path,
                                      @RequestParam("userId") @NotNull(message = "用户ID不能为空") Long userId,
                                      @RequestParam("uploadId") @NotNull(message = "上传ID不能为空") String uploadId,
                                      @RequestParam("source") @NotNull(message = "文件来源不能为空") Integer source) {
        return ResUtil.success(fileService.upload(file, path, userId, uploadId, source));
    }


    @InnerAuth
    @GetMapping("progress/{uploadId}")
    public ResData<UploadProgress> getFileUploadProgress(@PathVariable("uploadId") String uploadId) {
        return ResUtil.success(fileService.getFileUploadProgress(uploadId));
    }

    @InnerAuth
    @PostMapping("createHuaweiOBSTemporarySignature")
    public ResData<HuaweiOBSTemporarySignature> createHuaweiOBSTemporarySignature(
            @RequestBody @Validated CreateHuaweiOBSTemporarySignatureDTO createHuaweiOBSTemporarySignatureDTO) {
        return ResUtil.success(fileService.createHuaweiOBSTemporarySignature(createHuaweiOBSTemporarySignatureDTO.getPath(),
                createHuaweiOBSTemporarySignatureDTO.getFileName(), createHuaweiOBSTemporarySignatureDTO.getExpireSeconds(),
                createHuaweiOBSTemporarySignatureDTO.getContentType(),
                createHuaweiOBSTemporarySignatureDTO.getSource()));
    }

    @InnerAuth
    @PostMapping("completeHuaweiOBSUpload")
    public ResData<FilePO> completeHuaweiOBSUpload(
            @RequestBody @Validated CompleteUploadDTO completeUploadDTO) {
        return ResUtil.success(fileService.completeUpload(completeUploadDTO));
    }
}

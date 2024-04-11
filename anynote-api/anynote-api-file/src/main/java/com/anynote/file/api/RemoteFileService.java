package com.anynote.file.api;

import com.anynote.core.constant.ServiceNameConstants;
import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.config.MultipartSupportConfig;
import com.anynote.file.api.factory.RemoteFileFallbackFactory;
import com.anynote.file.api.model.bo.*;
import com.anynote.file.api.model.dto.CompleteUploadDTO;
import com.anynote.file.api.model.dto.CreateHuaweiOBSTemporarySignatureDTO;
import com.anynote.file.api.model.po.FilePO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author 称霸幼儿园
 */
@FeignClient(contextId = "remoteFileService",
        value = ServiceNameConstants.FILE_SERVICE,
        fallbackFactory = RemoteFileFallbackFactory.class,
        configuration = MultipartSupportConfig.class)
public interface RemoteFileService {

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResData<FilePO> uploadFile(@RequestPart("file") @NotNull(message = "文件不能为空") MultipartFile file,
                                      @RequestPart("path") @NotNull(message = "文件路径不能为空") String path,
                                      @RequestPart("userId") @NotNull(message = "用户id不能为空") Long userId,
                                      @RequestPart("uploadId") @NotNull(message = "上传ID不能为空") String uploadId,
                                      @RequestParam("source") @NotNull(message = "文件来源不能为空") Integer source);

    @GetMapping("files/progress/{uploadId}")
    public ResData<UploadProgress> getFileUploadProgress(@PathVariable("uploadId") String uploadId);

    @PostMapping("createHuaweiOBSTemporarySignature")
    public ResData<HuaweiOBSTemporarySignature> createHuaweiOBSTemporarySignature(
            @RequestBody @Validated CreateHuaweiOBSTemporarySignatureDTO createHuaweiOBSTemporarySignatureDTO);

    @PostMapping("completeHuaweiOBSUpload")
    public ResData<FilePO> completeHuaweiOBSUpload(@RequestBody @Validated CompleteUploadDTO completeUploadDTO);


    @GetMapping(value = "{id}")
    public ResData<FilePO> getFileById(@PathVariable("id") Long id, @RequestHeader("from-source") String fromSource);
}

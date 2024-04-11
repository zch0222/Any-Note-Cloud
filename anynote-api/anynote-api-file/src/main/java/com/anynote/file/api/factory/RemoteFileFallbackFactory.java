package com.anynote.file.api.factory;

import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.enums.ResCode;
import com.anynote.core.web.model.bo.CreateResEntity;
import com.anynote.core.web.model.bo.ResData;
import com.anynote.file.api.RemoteFileService;
import com.anynote.file.api.model.bo.*;
import com.anynote.file.api.model.dto.CompleteUploadDTO;
import com.anynote.file.api.model.dto.CreateHuaweiOBSTemporarySignatureDTO;
import com.anynote.file.api.model.po.FilePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务降级
 * @author 称霸幼儿园
 */
@Slf4j
@Component
public class RemoteFileFallbackFactory implements FallbackFactory<RemoteFileService> {


    @Override
    public RemoteFileService create(Throwable cause) {
        log.error("文件服务调用失败: {}", cause.getMessage());
        return new RemoteFileService() {

            @Override
            public ResData<FilePO> uploadFile(MultipartFile file, String path, Long userId, String uploadId, Integer source) {
                return ResUtil.error(ResCode.INNER_FILE_SERVICE_ERROR);
            }

            @Override
            public ResData<UploadProgress> getFileUploadProgress(String uploadId) {
                return ResUtil.error(ResCode.INNER_FILE_SERVICE_ERROR);
            }

            @Override
            public ResData<HuaweiOBSTemporarySignature> createHuaweiOBSTemporarySignature(CreateHuaweiOBSTemporarySignatureDTO createHuaweiOBSTemporarySignatureDTO) {
                return ResUtil.error(ResCode.INNER_FILE_SERVICE_ERROR);
            }

            @Override
            public ResData<FilePO> completeHuaweiOBSUpload(CompleteUploadDTO completeUploadDTO) {
                throw new BusinessException(ResCode.INNER_FILE_SERVICE_ERROR);
            }

            @Override
            public ResData<FilePO> getFileById(Long id, String fromSource) {
                throw new BusinessException("获取文件失败");
            }
        };
    }
}

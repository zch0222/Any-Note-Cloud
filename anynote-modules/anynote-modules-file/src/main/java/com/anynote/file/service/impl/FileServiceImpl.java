package com.anynote.file.service.impl;

import com.anynote.common.redis.service.RedisService;
import com.anynote.core.constant.CacheConstants;
import com.anynote.core.constant.RequestAttributesConstants;
import com.anynote.core.constant.SecurityConstants;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.ServletUtils;
import com.anynote.core.utils.StringUtils;
import com.anynote.core.utils.UrlUtil;
import com.anynote.file.api.model.bo.FileDTO;
import com.anynote.file.api.model.bo.HuaweiOBSTemporarySignature;
import com.anynote.file.api.model.bo.UploadProgress;
import com.anynote.file.api.model.dto.CompleteUploadDTO;
import com.anynote.file.api.model.po.FilePO;
import com.anynote.file.factory.FilePluginFactory;
import com.anynote.file.mapper.FileMapper;
import com.anynote.file.service.FileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 笔记图片文件服务
 * @author 称霸幼儿园
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FilePO>
        implements FileService {


    @Autowired
    private FilePluginFactory filePluginFactory;

    @Resource
    private RedisService redisService;

//    @Override
//    public FileDTO upload(NoteImageUploadParam uploadParam) {
//        NoteImage noteImage = NoteImage.builder()
//                .originalFileName(uploadParam.getFile().getOriginalFilename())
//                .fileName(UUID.randomUUID().toString().replace("-", "") + "_" + uploadParam.getFile().getOriginalFilename())
//                .us
//                .build();
//        String url = filePluginFactory.filePlugin()
//                .multipartFileUpload(uploadParam.getFile(),
//                        StringUtils.format("note/{}", uploadParam.getNoteId()),
//                        noteImage.getFileName());
//
//        return null;
//    }


    @Override
    public FilePO upload(CommonsMultipartFile file, String path, Long userId, String uploadId, Integer source) {
        ServletUtils.setRequestAttributes(RequestAttributesConstants.FILE_UPLOAD_ID_KEY, uploadId);
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
        String url = filePluginFactory.filePlugin()
                .multipartFileUpload(file, path, fileName);
        Date date = new Date();
        FilePO filePO = null;
        try {
            filePO = FilePO.builder()
                    .hash(DigestUtils.sha512Hex(file.getInputStream()))
                    .originalFileName(file.getOriginalFilename())
                    .fileName(fileName)
                    .url(url)
                    .source(source)
                    .deleted(0)
                    .type(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")))
                    .createBy(userId)
                    .createTime(date)
                    .updateBy(userId)
                    .updateTime(date)
                    .build();
        } catch (IOException e) {
            throw new BusinessException("计算文件Hash失败");
        }
        this.baseMapper.insert(filePO);
        return filePO;
    }

    @Override
    public UploadProgress getFileUploadProgress(String uploadId) {
        return redisService.getCacheObject(CacheConstants.FILE_UPLOAD_PROGRESS_KEY + uploadId);
    }

    @Override
    public HuaweiOBSTemporarySignature createHuaweiOBSTemporarySignature(String path, String fileName, Long expireSeconds,
                                                                         String contentType, Integer source) {
        Date date = new Date();
        FilePO filePO = FilePO.builder()
                .originalFileName(fileName)
                .fileName(UUID.randomUUID().toString().replace("-", "") + "_" + fileName)
                .createBy(Long.valueOf(ServletUtils.getHeader(SecurityConstants.DETAILS_USER_ID)))
                .deleted(0)
                .createTime(date)
                .type(contentType)
                .source(source)
                .build();
        String uploadId = UUID.randomUUID().toString().replace("-", "");
        HuaweiOBSTemporarySignature huaweiOBSTemporarySignature = filePluginFactory
                .huaweiFilePlugin().createTemporarySignature(path,
                        filePO.getFileName(),
                        expireSeconds, contentType);
        huaweiOBSTemporarySignature.setUploadId(uploadId);
        filePO.setUrl(UrlUtil.removeAllParams(huaweiOBSTemporarySignature.getSignedUrl()));

        redisService.setCacheObject(CacheConstants.FILE_UPLOAD_ID +
                        ServletUtils.getHeader(SecurityConstants.DETAILS_USER_ID) + ":" + uploadId, filePO,
                expireSeconds + 20L, TimeUnit.SECONDS);
        return huaweiOBSTemporarySignature;
    }

    @Override
    public FilePO completeUpload(CompleteUploadDTO completeUploadDTO) {
        Date date = new Date();
        FilePO filePO = redisService.getCacheObject(CacheConstants.FILE_UPLOAD_ID +
                ServletUtils.getHeader(SecurityConstants.DETAILS_USER_ID) + ":" +
                completeUploadDTO.getUploadId());
        if (StringUtils.isNull(filePO)) {
            throw new BusinessException("上传超时");
        }
        filePO.setHash(completeUploadDTO.getHash());
        filePO.setUpdateTime(date);
        filePO.setUpdateBy(Long.valueOf(ServletUtils.getHeader(SecurityConstants.DETAILS_USER_ID)));
        this.baseMapper.insert(filePO);
        redisService.deleteObject(CacheConstants.FILE_UPLOAD_ID +
                ServletUtils.getRequestAttributes(SecurityConstants.DETAILS_USER_ID) + ":" +
                completeUploadDTO.getUploadId());
        return filePO;
    }
}

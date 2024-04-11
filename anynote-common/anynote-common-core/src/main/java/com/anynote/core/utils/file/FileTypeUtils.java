package com.anynote.core.utils.file;

import com.anynote.core.validation.enums.FileType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author 称霸幼儿园
 */
public class FileTypeUtils {

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file)
    {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension))
        {
            extension = MimeTypeUtils.getExtension(Objects.requireNonNull(file.getContentType()));
        }
        return extension;
    }


    public static List<String> getAllowExtensions(FileType fileType) {
        if (FileType.IMAGE.equals(fileType)) {
            return Arrays.asList(MimeTypeUtils.IMAGE_EXTENSION);
        }
        else if (FileType.MEDIA.equals(fileType)) {
            return Arrays.asList(MimeTypeUtils.MEDIA_EXTENSION);
        }
        else if (FileType.VIDEO.equals(fileType)) {
            return Arrays.asList(MimeTypeUtils.VIDEO_EXTENSION);
        }
        else if (FileType.PDF.equals(fileType)) {
            return Arrays.asList(MimeTypeUtils.DOC_PDF_EXTENSION);
        }
        else {
            return Arrays.asList(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        }
    }
}

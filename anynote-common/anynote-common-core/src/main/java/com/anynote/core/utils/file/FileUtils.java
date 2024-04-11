package com.anynote.core.utils.file;

public class FileUtils {

    /**
     * 获取文件的后缀名。
     *
     * @param fileName 文件名
     * @return 文件的后缀名。如果没有找到合适的后缀名，则返回空字符串。
     */
    public static String getFileExtension(String fileName) {
        // 检查文件名是否为空或者没有包含点符号，这些情况下无法解析出后缀名
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        // 使用lastIndexOf()找到最后一个点符号的位置，然后从该位置开始到字符串末尾的子字符串即为后缀名
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}

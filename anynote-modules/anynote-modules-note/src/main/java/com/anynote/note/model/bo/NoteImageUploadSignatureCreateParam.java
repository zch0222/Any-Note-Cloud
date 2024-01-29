package com.anynote.note.model.bo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class NoteImageUploadSignatureCreateParam extends NoteQueryParam {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 图片类型
     */
    private String contentType;

    /**
     * 上传ID
     */
    private String uploadId;

    @Builder(builderMethodName = "NoteImageUploadSignatureCreateParamBuilder")
    public NoteImageUploadSignatureCreateParam(String fileName, String contentType, Long noteId, String uploadId) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.uploadId = uploadId;
        this.setId(noteId);
    }

    public void setNoteId(Long noteId) {
        this.setId(noteId);
    }

    public Long getNoteId() {
        return this.getId();
    }
}

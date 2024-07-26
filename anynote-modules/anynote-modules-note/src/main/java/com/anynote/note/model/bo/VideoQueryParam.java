package com.anynote.note.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoQueryParam {

    private Long folderId;

    private Long videoId;

    private Long knowledgeBaseId;

    private Integer page;

    private Integer pageSize;
}

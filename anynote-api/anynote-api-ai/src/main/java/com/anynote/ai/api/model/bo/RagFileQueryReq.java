package com.anynote.ai.api.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author 称霸幼儿园
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RagFileQueryReq {

    private String doc_url;

    @NotNull(message = "文件哈希不能为空")
    private String file_hash;

    @NotNull(message = "提示词不能为空")
    private String prompt;

    private String file_name;

    private String author;

    private String category;

    private String description;
}

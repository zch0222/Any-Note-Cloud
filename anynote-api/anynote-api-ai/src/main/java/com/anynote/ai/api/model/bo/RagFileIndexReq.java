package com.anynote.ai.api.model.bo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class RagFileIndexReq {

    @NotNull(message = "文件地址不能为空")
    private String file_path;
}

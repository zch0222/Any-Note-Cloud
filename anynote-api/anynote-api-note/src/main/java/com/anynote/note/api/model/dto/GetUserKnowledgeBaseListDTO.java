package com.anynote.note.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserKnowledgeBaseListDTO {

    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotNull(message = "知识库id列表不能为空")
    private List<Long> knowledgeBaseIds;


}

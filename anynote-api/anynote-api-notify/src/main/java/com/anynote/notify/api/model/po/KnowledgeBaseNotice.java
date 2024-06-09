package com.anynote.notify.api.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("ntc_knowledge_base_notice")
public class KnowledgeBaseNotice {

    private Long noticeId;

    private Long knowledgeBaseId;
}

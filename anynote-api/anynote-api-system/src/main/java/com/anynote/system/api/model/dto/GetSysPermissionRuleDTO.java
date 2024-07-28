package com.anynote.system.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetSysPermissionRuleDTO {

    /**
     * 规则名称
     */
    private String permissionRuleName;
}

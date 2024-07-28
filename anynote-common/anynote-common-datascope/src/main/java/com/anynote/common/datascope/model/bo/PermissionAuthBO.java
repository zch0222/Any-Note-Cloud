package com.anynote.common.datascope.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionAuthBO {

    /**
     * 鉴权成功
     */
    private boolean authenticationSuccess;

    private Integer permission;
}

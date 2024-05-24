package com.anynote.ai.api.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 称霸幼儿园
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RagFileQueryRes {

//    private String message;

    private String id;

    private String status;

    private String result;

}

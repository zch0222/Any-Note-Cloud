package com.anynote.ai.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeepLConfig implements Serializable {
    private static final long serialVersionUID = 0L;

    private String textTranslateEndPoint;

    private String token;
}

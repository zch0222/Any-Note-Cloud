package com.anynote.file.api.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuaweiOBSTemporarySignature {
    private String signedUrl;

    private Map<String, String> actualSignedRequestHeaders;

    private String uploadId;
}

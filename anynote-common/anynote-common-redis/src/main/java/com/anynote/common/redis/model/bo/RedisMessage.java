package com.anynote.common.redis.model.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RedisMessage {

    private String channel;

    private String message;
}

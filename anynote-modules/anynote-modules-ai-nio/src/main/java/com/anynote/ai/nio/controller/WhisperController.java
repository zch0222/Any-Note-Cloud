package com.anynote.ai.nio.controller;

import com.anynote.ai.nio.model.dto.WhisperDTO;
import com.anynote.ai.api.model.vo.WhisperSubmitVO;
import com.anynote.ai.nio.service.WhisperService;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("whisper")
public class WhisperController {

    @Resource
    private WhisperService whisperService;


    @PostMapping("")
    public Flux<ServerSentEvent<String>> whisper(@Validated @RequestBody WhisperDTO whisperDTO) {
        return whisperService.whisper(whisperDTO);
    }

    @PostMapping("submit")
    public Mono<ResData<WhisperSubmitVO>> submitWhisperTask(@Validated @RequestBody WhisperDTO whisperDTO,
                                                            @Validated @NotNull(message = "Token不能为空") @RequestHeader("accessToken") String accessToken) {
        return whisperService.submitWhisper(whisperDTO, accessToken)
                .flatMap(whisperSubmitVO -> Mono.just(ResUtil.success(whisperSubmitVO)));
    }
}

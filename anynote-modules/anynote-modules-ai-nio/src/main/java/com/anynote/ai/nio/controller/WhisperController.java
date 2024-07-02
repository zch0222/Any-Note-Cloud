package com.anynote.ai.nio.controller;

import com.anynote.ai.api.model.vo.WhisperTaskStatusVO;
import com.anynote.ai.nio.datascope.annotation.RequiresWhisperTaskPermissions;
import com.anynote.ai.nio.model.bo.WhisperTaskQueryParam;
import com.anynote.ai.nio.model.dto.WhisperDTO;
import com.anynote.ai.api.model.vo.WhisperSubmitVO;
import com.anynote.ai.nio.service.WhisperService;
import com.anynote.core.exception.BusinessException;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("whisper")
@Slf4j
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


    @GetMapping("/status/{taskId}")
    public Flux<ServerSentEvent<WhisperTaskStatusVO>> taskStatus(@PathVariable("taskId") Long taskId,
                                                                 @Validated @NotNull(message = "Token不能为空") @RequestHeader("accessToken") String accessToken) {
        log.info("TEST");
        return whisperService.whisperTaskStatus(WhisperTaskQueryParam.builder()
                .whisperTaskId(taskId).accessToken(accessToken)
                .build());
    }


}

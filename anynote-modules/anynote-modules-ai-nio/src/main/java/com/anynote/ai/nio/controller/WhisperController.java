package com.anynote.ai.nio.controller;

import com.anynote.ai.nio.model.dto.WhisperDTO;
import com.anynote.ai.nio.service.WhisperService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

@RestController
@RequestMapping("whisper")
public class WhisperController {

    @Resource
    private WhisperService whisperService;


    @PostMapping("")
    public Flux<ServerSentEvent<String>> whisper(@Validated @RequestBody WhisperDTO whisperDTO) {
        return whisperService.whisper(whisperDTO);
    }
}

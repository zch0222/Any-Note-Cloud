package com.anynote.ai.nio.controller;

import com.anynote.ai.nio.model.dto.WhisperDTO;
import com.anynote.ai.nio.service.WhisperService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

@RestController
@RequestMapping("whisper")
public class WhisperController {

    @Resource
    private WhisperService whisperService;


    @GetMapping("")
    public Flux<ServerSentEvent<String>> whisper(@Validated @RequestBody WhisperDTO whisperDTO) {
        return whisperService.whisper(whisperDTO);
    }
}

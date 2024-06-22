package com.anynote.ai.nio.service;

import com.anynote.ai.nio.model.dto.WhisperDTO;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface WhisperService {


    public Flux<ServerSentEvent<String>> whisper(WhisperDTO whisperDTO);


}

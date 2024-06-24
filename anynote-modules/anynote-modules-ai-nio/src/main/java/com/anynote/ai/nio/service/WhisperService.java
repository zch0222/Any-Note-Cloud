package com.anynote.ai.nio.service;

import com.anynote.ai.nio.model.dto.WhisperDTO;
import com.anynote.ai.api.model.vo.WhisperSubmitVO;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WhisperService {


    public Flux<ServerSentEvent<String>> whisper(WhisperDTO whisperDTO);

    public Mono<WhisperSubmitVO> submitWhisper(WhisperDTO whisperDTO, String accessToken);


}

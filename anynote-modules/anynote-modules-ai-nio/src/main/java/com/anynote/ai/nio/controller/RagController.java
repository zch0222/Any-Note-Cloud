package com.anynote.ai.nio.controller;

import com.anynote.ai.api.model.bo.DocRagQueryParam;
import com.anynote.ai.api.model.dto.DocQueryDTO;
import com.anynote.ai.api.model.vo.DocQueryVO;
import com.anynote.ai.nio.model.vo.AIChatVO;
import com.anynote.ai.nio.service.RagService;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import javax.annotation.Resource;


@Slf4j
@RestController
@RequestMapping("/rag")
public class RagController {

    @Resource
    private RagService ragService;


    private String test1() throws InterruptedException {

        Thread.sleep(10000);
        log.info("TEST1");
        return "TTT";

    }

    @GetMapping("test")
    public Mono<String> test() {
        log.info("test");
        Context context = Context.of("Test", "TEST!!!");
//        System.out.println("Test");
        return Mono.fromCallable(this::test1).publishOn(Schedulers.boundedElastic());
    }

    @PostMapping("/doc")
    public Flux<ResData<AIChatVO>> queryDoc(@Validated @RequestBody DocQueryDTO docQueryDTO,
                                            @RequestHeader("accessToken") String accessToken) {
        log.info(accessToken);
        return ragService.queryDoc(DocRagQueryParam.DocRagQueryParamBuilder()
                .conversionId(docQueryDTO.getConversationId())
                .docId(docQueryDTO.getDocId())
                .prompt(docQueryDTO.getPrompt())
                .build(), accessToken)
                .flatMap(value -> Flux.just(ResUtil.success(value)));
    }

    @PostMapping("query/docs/v1")
    public Flux<ResData<DocQueryVO>> queryDocV1(@Validated @RequestBody DocQueryDTO docQueryDTO) {
        return null;
    }

}

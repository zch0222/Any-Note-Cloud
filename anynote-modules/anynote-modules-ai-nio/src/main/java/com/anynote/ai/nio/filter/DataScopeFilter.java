package com.anynote.ai.nio.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class DataScopeFilter implements WebFilter {



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("MAIN");
//        Mono.fromCallable(this::test).subscribe(value -> {
//            log.info("sub" + value);
//            if (chain.filter())
//        });
        return chain.filter(exchange);
    }

    public boolean test() {
        log.info("TEST FILTER");
        return true;
    }
}

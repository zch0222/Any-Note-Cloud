package com.anynote.core.filter.webflux;

import com.anynote.core.condition.SpringWebfluxCondition;
import com.anynote.core.constant.SecurityConstants;
import com.anynote.core.utils.StringUtils;
import lombok.NonNull;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * SpringWebflux 获取header
 */
@Component
@Conditional(SpringWebfluxCondition.class)
public class ContextWebFilter implements WebFilter {


    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String accessToken = exchange.getRequest().getHeaders().getFirst(SecurityConstants.ACCESS_TOKEN);
        if (StringUtils.isNotNull(accessToken)) {
            return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put(SecurityConstants.ACCESS_TOKEN, accessToken));
        }
        return chain.filter(exchange);

    }
}

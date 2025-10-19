package com.cesar.springcloud.gateway_server.filter;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        logger.info("[PRE] Ejecutando filtro antes del request");
        String token = UUID.randomUUID().toString();

        var requestMutated = exchange.getRequest().mutate().headers(header -> header.add("token", token)).build();
        var exchangeMutated = exchange.mutate().request(requestMutated).build();

        return chain.filter(exchangeMutated).then(Mono.fromRunnable(() -> {
            logger.info("[POST] Ejecutando filtro después del response");

            Optional.ofNullable(exchangeMutated.getRequest().getHeaders().getFirst("token")).ifPresent(tkn -> {
                logger.info("token: " + tkn);
                exchangeMutated.getResponse().getHeaders().add("token", token);
            });

            exchangeMutated.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            //exchangeMutated.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);

        }));

    }

    @Override
    public int getOrder() {
        return 100;
    }

}

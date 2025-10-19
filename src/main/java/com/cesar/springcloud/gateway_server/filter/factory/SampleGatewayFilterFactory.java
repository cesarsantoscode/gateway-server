package com.cesar.springcloud.gateway_server.filter.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class SampleGatewayFilterFactory
        extends AbstractGatewayFilterFactory<SampleGatewayFilterFactory.ConfigurationCookie> {

    private final Logger logger = LoggerFactory.getLogger(SampleGatewayFilterFactory.class);

    public SampleGatewayFilterFactory() {
        super(ConfigurationCookie.class);
    }

    @Override
    public GatewayFilter apply(ConfigurationCookie config) {

        return new OrderedGatewayFilter((exchange, chain) -> {

            logger.info("[PRE] Ejecutando filtro GW antes del request: " + config.message);

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                Optional.ofNullable(config.value).ifPresent(value -> {
                    exchange.getResponse().addCookie(ResponseCookie.from(config.name, value).build());
                    logger.info("[POST] Ejecutando filtro GW después del response: " + config.message);
                });

            }));

        }, 100);

    }

    public static class ConfigurationCookie {

        private String name;
        private String value;
        private String message;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("message", "value", "name");
    }

    @Override
    public String name() {
        return "FiltroEjemplo";
    }

}

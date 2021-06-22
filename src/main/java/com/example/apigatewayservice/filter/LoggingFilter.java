package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config>{

    public LoggingFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        GatewayFilter filter= new OrderedGatewayFilter((exchange,chain)->{
            ServerHttpRequest request=exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("logging filter basemessage", config.getBaseMessage());

            if(config.isPreLogger()){
                log.info("logging Filter Start : request id " + request.getId());
            }
            //Mono : 비동기 방식 지
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if(config.isPostLogger()){
                    log.info("logging Filter end : request id "+ response.getStatusCode());
                }
            }));
        },Ordered.LOWEST_PRECEDENCE);

        // Ordered 우선순위에 따라서 동작 순서가 달라짐
        // Ordered.LOWEST_PRECEDENCE : 가장 마지막 실행, 가장 늦게 끝남. Ordered.HIGHEST_PRECEDENCE : 가장 먼저 실행, 가장늦게 끝남
        return filter;
    }

    @Data
    public static class Config{
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
        private String sayHello;
    }
}



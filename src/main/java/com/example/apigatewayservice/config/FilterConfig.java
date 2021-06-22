package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration : yml 파일로 해보려고 주석처리
public class FilterConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r->r.path("/first-service/**")
                        .filters(f->f.addRequestHeader("first-request","first-request-value")
                        .addResponseHeader("first-response","first-response-value"))
                        .uri("http://localhost:8081"))
                .route(r->r.path("/second-service/**")
                        .filters(f->f.addRequestHeader("second-request","second-request-value")
                                .addResponseHeader("second-response","second-response-value"))
                        .uri("http://localhost:8082"))
                .build();
    }
}

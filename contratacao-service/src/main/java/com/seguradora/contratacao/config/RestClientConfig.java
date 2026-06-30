package com.seguradora.contratacao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.proposta-service.url:http://localhost:8081}")
    private String propostaServiceUrl;

    private final CorrelationInterceptor correlationInterceptor;

    public RestClientConfig(CorrelationInterceptor correlationInterceptor) {
        this.correlationInterceptor = correlationInterceptor;
    }

    @Bean
    public RestClient proposalRestClient() {
        return RestClient.builder()
                .baseUrl(propostaServiceUrl)
                .requestInterceptor(correlationInterceptor)
                .build();
    }
}

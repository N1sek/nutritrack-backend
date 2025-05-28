package com.nutritrack.nutritrackbackend.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // 1) Conexión: 15 s máximo para conectar
        // 2) Solicitud: 60 s máximo para recibir la respuesta completa
        // 3) Pool: para usar conexiones keep-alive y no abrir siempre de nuevo
        var requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(15))
                .setResponseTimeout(Timeout.ofSeconds(60))
                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                .build();

        var client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();

        var factory = new HttpComponentsClientHttpRequestFactory(client);
        return new RestTemplate(factory);
    }
}

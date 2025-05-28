package com.nutritrack.nutritrackbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class RetryConfig {
    // Solo con @EnableRetry basta para que @Retryable funcione
}

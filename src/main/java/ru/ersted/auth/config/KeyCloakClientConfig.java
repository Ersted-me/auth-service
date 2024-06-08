package ru.ersted.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KeyCloakClientConfig {
    @Bean
    public WebClient simpleClient(@Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}") String ISSUER_URI){
        return WebClient.builder()
                .baseUrl(ISSUER_URI)
                .build();
    }

    @Bean
    public WebClient adminClient(@Value("${keycloak-service.client.base-admin-url}") String BASE_ADMIN_URI){
        return WebClient.builder()
                .baseUrl(BASE_ADMIN_URI)
                .build();
    }
}

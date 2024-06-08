package ru.ersted.auth.client;

import net.ersted.dto.auth.JwtDto;
import net.ersted.dto.keycloak.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@Component
public class KeyCloakClient {
    private final static String TOKEN_ENDPOINT = "/protocol/openid-connect/token";
    private final static String CREATE_USER = "/users";
    private final static String BEARER_PREFIX = "Bearer ";
    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final WebClient client;
    private final WebClient adminClient;

    public KeyCloakClient(@Value("${spring.security.oauth2.client.registration.keycloak.client-secret}") String clientSecret,
                          @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String clientId,
                          @Qualifier("simpleClient") WebClient client,
                          @Qualifier("adminClient") WebClient adminClient) {
        CLIENT_SECRET = clientSecret;
        CLIENT_ID = clientId;
        this.client = client;
        this.adminClient = adminClient;
    }

    public Mono<JwtDto> tokenByGrantTypePassword(String username, String password) {
        return client.post()
                .uri(TOKEN_ENDPOINT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(fromFormData("grant_type", "password")
                        .with("client_id", CLIENT_ID)
                        .with("client_secret", CLIENT_SECRET)
                        .with("username", username)
                        .with("password", password))
                .retrieve()
                .bodyToMono(JwtDto.class);
    }

    private Mono<JwtDto> tokenByGrantTypeClientCredentials() {
        return client.post()
                .uri(TOKEN_ENDPOINT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(fromFormData("grant_type", "client_credentials")
                        .with("client_id", CLIENT_ID)
                        .with("client_secret", CLIENT_SECRET))
                .retrieve()
                .bodyToMono(JwtDto.class)
                .doOnSuccess(System.out::println);
    }

    public Mono<Void> createUser(UserRepresentation rq) {
        return tokenByGrantTypeClientCredentials().flatMap(jwtDto ->
                        adminClient.post()
                                .uri(CREATE_USER)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtDto.getAccessToken())
                                .bodyValue(rq)
                                .retrieve()
                                .bodyToMono(String.class)
                                .doOnSuccess(System.out::println)
                )
                .then();
    }
}
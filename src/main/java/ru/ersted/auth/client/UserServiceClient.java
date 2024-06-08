package ru.ersted.auth.client;

import net.ersted.dto.IndividualDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {
    private final String REGISTRATION_ENDPOINT;
    private final String DETAILS_ENDPOINT;
    private final WebClient webClient;

    public UserServiceClient(@Qualifier("baseUserClient") WebClient useClient,
                             @Value("${user-service.client.registration-endpoint}") String registrationEndpoint,
                             @Value("${user-service.client.details-endpoint}") String detailsEndpoint) {
        REGISTRATION_ENDPOINT = registrationEndpoint;
        DETAILS_ENDPOINT = detailsEndpoint;
        this.webClient = useClient;
    }

    public Mono<IndividualDto> registration(IndividualDto body) {
        return webClient.post()
                .uri(REGISTRATION_ENDPOINT)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(IndividualDto.class);
    }

    public Mono<IndividualDto> details(String id) {
        return webClient.get()
                .uri(DETAILS_ENDPOINT.formatted(id))
                .retrieve()
                .bodyToMono(IndividualDto.class);
    }
}
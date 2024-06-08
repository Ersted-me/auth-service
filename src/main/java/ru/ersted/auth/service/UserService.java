package ru.ersted.auth.service;

import lombok.RequiredArgsConstructor;
import net.ersted.dto.IndividualDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ersted.auth.client.UserServiceClient;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;

    public Mono<IndividualDto> registration(String email) {
        IndividualDto body = new IndividualDto();
        body.setEmail(email);
        return userServiceClient.registration(body);
    }

    public Mono<IndividualDto> details(String id) {
        return userServiceClient.details(id);
    }
}

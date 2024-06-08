package ru.ersted.auth.service;

import lombok.AllArgsConstructor;
import net.ersted.dto.auth.JwtDto;
import net.ersted.dto.auth.RegistrationUserDto;
import net.ersted.dto.keycloak.CredentialRepresentation;
import net.ersted.dto.keycloak.UserRepresentation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ersted.auth.client.KeyCloakClient;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserService userService;
    private final KeyCloakClient keyCloakClient;

    public Mono<JwtDto> registrationUser(RegistrationUserDto rq) {
        if (!rq.getPassword().equals(rq.getConfirmPassword())) {
            return Mono.error(new RuntimeException("Password and confirm password not equal"));
        }
        return keyCloakClient.createUser(new UserRepresentation(rq.getEmail(), rq.getEmail(), rq.getEmail(), rq.getEmail(), true,
                List.of(new CredentialRepresentation("password", rq.getPassword(), false))))
                .then(Mono.defer(() -> userService.registration(rq.getEmail())))
                .then(Mono.defer(() -> keyCloakClient.tokenByGrantTypePassword(rq.getEmail(), rq.getPassword())));
    }
}

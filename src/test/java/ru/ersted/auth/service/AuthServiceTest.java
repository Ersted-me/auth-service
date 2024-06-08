package ru.ersted.auth.service;

import net.ersted.dto.IndividualDto;
import net.ersted.dto.auth.JwtDto;
import net.ersted.dto.auth.RegistrationUserDto;
import net.ersted.dto.keycloak.CredentialRepresentation;
import net.ersted.dto.keycloak.UserRepresentation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.ersted.auth.client.KeyCloakClient;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private KeyCloakClient keyCloakClient;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Регистрация пользователя в keycloak и user-service")
    public void givenRegistrationDto_whenRegistration_thenJwtDtoIsReturned() {
        //given
        RegistrationUserDto rq = new RegistrationUserDto("some@mail.ru", "password", "password");
        BDDMockito.given(keyCloakClient.createUser(new UserRepresentation("some@mail.ru", "some@mail.ru", "some@mail.ru", "some@mail.ru", true,
                        List.of(new CredentialRepresentation("password", "password", false)))))
                .willReturn(Mono.empty());
        BDDMockito.given(userService.registration("some@mail.ru"))
                .willReturn(Mono.just(new IndividualDto("UUID", null, null, "some@mail.ru", null)));
        BDDMockito.given(keyCloakClient.tokenByGrantTypePassword("some@mail.ru", "password"))
                .willReturn(Mono.just(new JwtDto("accessToken", 300, "refreshToken", "tokenType")));
        //when
        StepVerifier.create(authService.registrationUser(rq))
                //then
                .expectNextMatches(jwtDto -> new JwtDto("accessToken", 300, "refreshToken", "tokenType").equals(jwtDto))
                .verifyComplete();
    }
}
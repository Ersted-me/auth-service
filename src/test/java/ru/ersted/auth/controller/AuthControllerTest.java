package ru.ersted.auth.controller;

import net.ersted.dto.auth.JwtDto;
import net.ersted.dto.auth.RegistrationUserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.ersted.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Регистрация пользователя")
    public void givenRegistrationUserDto_whenRegistration_thenJwtDtoIsReturned() {
        //given
        RegistrationUserDto regDto = new RegistrationUserDto("some@mail.ru", "password", "password");

        BDDMockito.given(authService.registrationUser(new RegistrationUserDto("some@mail.ru", "password", "password")))
                .willReturn(Mono.just(new JwtDto("accessToken", 300, "refreshToken", "tokenType")));
        //when
        StepVerifier.create(authController.registration(regDto))
                //then
                .expectNextMatches(jwtDto -> jwtDto.equals(new JwtDto("accessToken", 300, "refreshToken", "tokenType")))
                .verifyComplete();
    }
}
package ru.ersted.auth.it;

import net.ersted.dto.IndividualDto;
import net.ersted.dto.auth.RegistrationUserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.ersted.auth.client.UserServiceClient;
import ru.ersted.auth.config.KeycloakTestContainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class ItAuthControllerTest extends KeycloakTestContainers {

    @Autowired
    private WebTestClient webTestClient;
    @SpyBean
    private UserServiceClient userServiceClient;

    @Test
    @DisplayName("Регистрация пользователя")
    public void givenRegistrationUserDto_whenRegistration_thenJwtDtoIsReturned() {
        //given
        RegistrationUserDto registrationUserDto = new RegistrationUserDto("some@mail.ru", "password", "password");
        BDDMockito.given(userServiceClient.registration(new IndividualDto(null, null, null, "some@mail.ru", null)))
                .willReturn(Mono.just(new IndividualDto("UUID", null, null, "some@mail.ru", null)));
        //when
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationUserDto), RegistrationUserDto.class)
                .exchange();
        //then
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").isNotEmpty()
                .jsonPath("$.expires_in").isNotEmpty()
                .jsonPath("$.refresh_token").isNotEmpty()
                .jsonPath("$.token_type").isNotEmpty();
    }
}

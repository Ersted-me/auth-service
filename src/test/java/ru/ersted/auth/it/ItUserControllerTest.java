package ru.ersted.auth.it;

import net.ersted.dto.IndividualDto;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.ersted.auth.client.UserServiceClient;
import ru.ersted.auth.config.KeycloakTestContainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
class ItUserControllerTest extends KeycloakTestContainers {

    @Autowired
    private WebTestClient webTestClient;

    @SpyBean
    private UserServiceClient userServiceClient;

    @Test
    @DisplayName("Получение данных по зарегистрированному пользователю")
    public void givenRegistrationUserDto_whenRegistration_thenJwtDtoIsReturned() {
        //given
        BDDMockito.given(userServiceClient.details("UUID"))
                .willReturn(Mono.just(new IndividualDto("UUID", null, null, "some@mail.ru", null)));
        //when
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/v1/individuals/%s".formatted("UUID"))
                .header(HttpHeaders.AUTHORIZATION, KeycloakTestContainers.getErstedWayBearer())
                .exchange();
        //then
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("UUID")
                .jsonPath("$.email").isEqualTo("some@mail.ru");
    }
}
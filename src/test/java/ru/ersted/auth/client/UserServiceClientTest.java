package ru.ersted.auth.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ersted.dto.IndividualDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.springframework.test.util.AssertionErrors.assertEquals;

class UserServiceClientTest {

    public static String REGISTRATION_ENDPOINT = "/api/v1/individuals";
    public static String DETAILS_ENDPOINT = "/api/v1/individuals/%s";
    public static MockWebServer mockBackEnd;

    private UserServiceClient userServiceClient;


    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        userServiceClient = new UserServiceClient(webClient, REGISTRATION_ENDPOINT, DETAILS_ENDPOINT);
    }

    @Test
    @DisplayName("Регистрация пользователя по email")
    public void givenEmail_whenRegistration_thenIndividualDtoIsReturned() throws JsonProcessingException, InterruptedException {
        //given
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new IndividualDto("UUID", null, null, "some@mail.ru", null)))
                .addHeader("Content-Type", "application/json"));
        //when
        StepVerifier.create(userServiceClient.registration(new IndividualDto(null, null, null, "some@mail.ru", null)))
                //then
                .expectNextMatches(dto -> new IndividualDto("UUID", null, null, "some@mail.ru", null).equals(dto))
                .verifyComplete();
        assertEquals("Проверка пути запроса", REGISTRATION_ENDPOINT, mockBackEnd.takeRequest().getPath());
    }

    @Test
    @DisplayName("Получение пользовательской информации по ID")
    public void givenID_whenDetails_thenIndividualDtoIsReturned() throws JsonProcessingException, InterruptedException {
        //given
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new IndividualDto("UUID", null, null, "some@mail.ru", null)))
                .addHeader("Content-Type", "application/json"));
        //when
        StepVerifier.create(userServiceClient.details("UUID"))
                //then
                .expectNextMatches(dto -> new IndividualDto("UUID", null, null, "some@mail.ru", null).equals(dto))
                .verifyComplete();
        assertEquals("Проверка пути запроса", DETAILS_ENDPOINT.formatted("UUID"), mockBackEnd.takeRequest().getPath());
    }
}
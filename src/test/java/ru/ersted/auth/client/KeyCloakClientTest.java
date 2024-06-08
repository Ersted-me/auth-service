package ru.ersted.auth.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ersted.dto.auth.JwtDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.springframework.test.util.AssertionErrors.assertEquals;


public class KeyCloakClientTest {

    private final static String REALM_NAME = "for-it-tests-realm";
    private final static String TOKEN_ENDPOINT = "/realms/%s/protocol/openid-connect/token".formatted(REALM_NAME);

    public static MockWebServer mockBackEnd;
    private KeyCloakClient keyCloakClient;

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
        WebClient client = WebClient.builder()
                .baseUrl(baseUrl + "/realms/" + REALM_NAME)
                .build();
        WebClient admin = WebClient.builder()
                .baseUrl(baseUrl + "/admin/realms/" + REALM_NAME)
                .build();
        keyCloakClient = new KeyCloakClient("clientSecret", "clientId", client, admin);
    }

    @Test
    @DisplayName("Получение токена по username и password")
    public void givenUsernameAndPassword_whenTokenByGrantTypePassword_thenJwtDtoIsReturned() throws JsonProcessingException, InterruptedException {
        //given
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new JwtDto("accessToken", 300, "refreshToken", "tokenType")))
                .addHeader("Content-Type", "application/json"));
        //when
        StepVerifier.create(keyCloakClient.tokenByGrantTypePassword("username", "password"))
                //then
                .expectNextMatches(jwtDto -> new JwtDto("accessToken", 300, "refreshToken", "tokenType").equals(jwtDto))
                .verifyComplete();
        assertEquals("Проверка пути запроса", TOKEN_ENDPOINT, mockBackEnd.takeRequest().getPath());
    }
}
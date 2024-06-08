package ru.ersted.auth.service;

import net.ersted.dto.IndividualDto;
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
import ru.ersted.auth.client.UserServiceClient;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Регистрация пользователя по email")
    public void givenUserEmail_whenRegistration_thenIndividualDtoIsReturned() {
        //given
        RegistrationUserDto rq = new RegistrationUserDto("some@mail.ru", "password", "password");

        IndividualDto individualDto = new IndividualDto();
        individualDto.setId("UUID");
        individualDto.setEmail("some@mail.ru");
        BDDMockito.given(userServiceClient.registration(new IndividualDto(null,null,null,"some@mail.ru",null)))
                .willReturn(Mono.just(individualDto));
        //when
        StepVerifier.create(userService.registration("some@mail.ru"))
                //then
                .expectNextMatches(dto -> "UUID".equals(dto.getId()) && "some@mail.ru".equals(dto.getEmail()))
                .verifyComplete();
    }


    @Test
    @DisplayName("Поиск данных пользователя по id")
    public void givenUserId_whenDetails_thenIndividualDtoIsReturned() {
        //given
        String id = "UUID";
        IndividualDto individualDto = new IndividualDto();
        individualDto.setId("UUID");
        BDDMockito.given(userServiceClient.details("UUID"))
                .willReturn(Mono.just(individualDto));
        //when
        StepVerifier.create(userService.details(id))
                //then
                .expectNextMatches(dto -> "UUID".equals(dto.getId()))
                .verifyComplete();
    }
}
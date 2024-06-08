package ru.ersted.auth.controller;

import net.ersted.dto.IndividualDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.ersted.auth.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Получение информации по пользователю")
    public void givenUserId_whenDetails_thenIndividualDtoIsReturned(){
        //given
        String userId = "someId";
        BDDMockito.given(userService.details("someId"))
                .willReturn(Mono.just(new IndividualDto("someId",null,null,"some@mail.ru",null)));
        //when
        StepVerifier.create(userController.find(userId))
        //then
                .expectNextMatches(dto -> dto.equals(new IndividualDto("someId",null,null,"some@mail.ru",null)))
                .verifyComplete();
    }

}
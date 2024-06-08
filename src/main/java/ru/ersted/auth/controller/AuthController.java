package ru.ersted.auth.controller;

import lombok.RequiredArgsConstructor;
import net.ersted.dto.auth.RegistrationUserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.ersted.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registration")
    public Mono<?> registration(@RequestBody RegistrationUserDto rq) {
        return authService.registrationUser(rq);
    }
}
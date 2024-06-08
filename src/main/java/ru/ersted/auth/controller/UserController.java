package ru.ersted.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.ersted.auth.service.UserService;

@RestController
@RequestMapping("/api/v1/individuals")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/{userId}")
    public Mono<?> find(@PathVariable("userId") String userId) {
        return userService.details(userId);
    }
}
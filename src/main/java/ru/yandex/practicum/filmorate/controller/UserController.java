package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<String, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.debug("Текущий пользователь: {}", user);

        UserValidation.validation(user);

        if (users.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с таким email уже существует!");
        }

        users.put(user.getEmail(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.debug("Текущий пользователь: {}", user);

        UserValidation.validation(user);

        users.put(user.getEmail(), user);

        return user;
    }
}

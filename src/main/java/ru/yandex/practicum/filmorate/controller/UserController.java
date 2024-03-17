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
    private long userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody User requestUser) {
        UserValidation.validation(requestUser);
        User user = requestUser.toBuilder()
                .id(userId++)
                .name(requestUser.getName() == null ? requestUser.getLogin() : requestUser.getName())
                .build();

        log.info("Текущий пользователь: {}", user);

        for (User userItem : users.values()) {
            if (userItem.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException("Пользователь с таким email уже существует!");
            }
        }

        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User requestUser) {
        log.info("Текущий пользователь: {}", requestUser);

        if (!users.containsKey(requestUser.getId())) {
            throw new ValidationUserException("Пользователя не существует!");
        }

        UserValidation.validation(requestUser);

        users.put(requestUser.getId(), requestUser);

        return requestUser;
    }
}

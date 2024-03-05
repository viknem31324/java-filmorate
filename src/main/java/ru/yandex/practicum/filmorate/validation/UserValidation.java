package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidation {
    private static final LocalDate nowData = LocalDate.now();

    public static void validation(User user) throws ValidationUserException {
        if (user.getEmail().indexOf('@') == -1) {
            log.debug("Текущий email: {}", user.getEmail());
            throw new ValidationUserException("Некорректный email!");
        }

        if (user.getLogin().isBlank()) {
            log.debug("Текущий логин: {}", user.getLogin());
            throw new ValidationUserException("Некорректный логин!");
        }

        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(nowData)) {
            log.debug("Текущий день рождения: {}", user.getBirthday());
            throw new ValidationUserException("Некорректный день рождения!");
        }
    }
}

package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidation {
    private static final LocalDate nowData = LocalDate.now();

    public static void validation(User user) throws ValidationUserException {
        System.out.println(user);
        if (user.getEmail().indexOf('@') == -1) {
            throw new ValidationUserException("Некорректный email!");
        }

        if (user.getLogin().isBlank()) {
            throw new ValidationUserException("Некорректный логин!");
        }

        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(nowData)) {
            throw new ValidationUserException("Некорректный день рождения!");
        }
    }
}

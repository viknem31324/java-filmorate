package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Slf4j
public class UserValidation {
    private static final String REGEX_EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static final LocalDate nowData = LocalDate.now();

    public static void validation(User user) throws ValidationUserException {
        if (user.getEmail() == null) {
            throw new ValidationUserException("Не заполнен email!");
        }

        if (!patternMatches(user.getEmail(), REGEX_EMAIL_PATTERN)) {
            log.info("Текущий email: {}", user.getEmail());
            throw new ValidationUserException("Некорректный email!");
        }

        if (user.getLogin() == null) {
            throw new ValidationUserException("Не заполнен логин!");
        }

        int countWhitespace = StringUtils.countOccurrencesOf(user.getLogin(), " ");
        if (countWhitespace > 0) {
            log.info("Текущий логин: {}", user.getLogin());
            throw new ValidationUserException("Логин не может содержать пробелы!");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(nowData)) {
            log.info("Текущий день рождения: {}", user.getBirthday());
            throw new ValidationUserException("Некорректный день рождения!");
        }
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}

package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserValidationTest {
    @Test
    public void checkValidationEmail() {
        User user = new User(1, null, "Login");

        User finalUser = user;
        ValidationUserException emptyEmail = assertThrows(
                ValidationUserException.class,
                () -> UserValidation.validation(finalUser)
        );

        assertEquals("Не заполнен email!", emptyEmail.getMessage());

        user = new User(1, "testyandex.ru", "Login");

        User finalUser1 = user;
        ValidationUserException incorrectEmail = assertThrows(
                ValidationUserException.class,
                () -> UserValidation.validation(finalUser1)
        );

        assertEquals("Некорректный email!", incorrectEmail.getMessage());
    }

    @Test
    public void checkValidationLogin() {
        User user = new User(1, "test@yandex.ru", null);

        User finalUser = user;
        ValidationUserException emptyLogin = assertThrows(
                ValidationUserException.class,
                () -> UserValidation.validation(finalUser)
        );

        assertEquals("Не заполнен логин!", emptyLogin.getMessage());

        user = new User(1, "test@yandex.ru", "   Logi n   ");

        User finalUser1 = user;
        ValidationUserException incorrectLogin = assertThrows(
                ValidationUserException.class,
                () -> UserValidation.validation(finalUser1)
        );

        assertEquals("Логин не может содержать пробелы!", incorrectLogin.getMessage());
    }

    @Test
    public void checkEmptyName() {
        String login = "Login";
        User user = new User(1, "test@yandex.ru", login);
        UserValidation.validation(user);
        assertEquals(login, user.getName());
    }

    @Test
    public void checkCorrectBirthday() {
        User user = new User(1, "test@yandex.ru", "Login");
        user.setBirthday(LocalDate.of(2025, Month.DECEMBER, 12));

        ValidationUserException incorrectBirthday = assertThrows(
                ValidationUserException.class,
                () -> UserValidation.validation(user)
        );

        assertEquals("Некорректный день рождения!", incorrectBirthday.getMessage());
    }
}

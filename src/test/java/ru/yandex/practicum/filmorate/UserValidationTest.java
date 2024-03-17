package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserValidationTest {
    private InMemoryUserStorage storage;

    @BeforeEach
    public void init() {
        storage = new InMemoryUserStorage();
    }

    @Test
    public void checkValidationEmail() {
        User user = User.builder()
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1998, Month.MAY, 9))
                .build();

        ValidationUserException emptyEmail = assertThrows(
                ValidationUserException.class,
                () -> storage.createUser(user)
        );

        assertEquals("Не заполнен email!", emptyEmail.getMessage());

        User userNew = user.toBuilder()
                .email("testyandex.ru")
                .build();

        ValidationUserException incorrectEmail = assertThrows(
                ValidationUserException.class,
                () -> storage.createUser(userNew)
        );

        assertEquals("Некорректный email!", incorrectEmail.getMessage());
    }

    @Test
    public void checkValidationLogin() {
        User user = User.builder()
                .email("test@yandex.ru")
                .name("name")
                .birthday(LocalDate.of(1998, Month.MAY, 9))
                .build();

        ValidationUserException emptyLogin = assertThrows(
                ValidationUserException.class,
                () -> storage.createUser(user)
        );

        assertEquals("Не заполнен логин!", emptyLogin.getMessage());

        User userNew = user.toBuilder()
                .login("   log in   ")
                .build();

        ValidationUserException incorrectLogin = assertThrows(
                ValidationUserException.class,
                () -> storage.createUser(userNew)
        );

        assertEquals("Логин не может содержать пробелы!", incorrectLogin.getMessage());
    }

    @Test
    public void checkEmptyName() {
        String login = "Login";
        User user = User.builder()
                .email("test@yandex.ru")
                .login(login)
                .birthday(LocalDate.of(1998, Month.MAY, 9))
                .build();
        User newUser = storage.createUser(user);
        assertEquals(login, newUser.getName());
    }

    @Test
    public void checkCorrectBirthday() {
        User user = User.builder()
                .email("test@yandex.ru")
                .login("login")
                .birthday(LocalDate.of(2025, Month.MAY, 9))
                .build();

        ValidationUserException incorrectBirthday = assertThrows(
                ValidationUserException.class,
                () -> storage.createUser(user)
        );

        assertEquals("Некорректный день рождения!", incorrectBirthday.getMessage());
    }
}

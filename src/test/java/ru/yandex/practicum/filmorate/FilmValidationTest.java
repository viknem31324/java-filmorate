package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmValidationTest {
    @Test
    public void checkEmptyName() {
        Film film = new Film(1, null);

        ValidationFilmException emptyName = assertThrows(
                ValidationFilmException.class,
                () -> FilmValidation.validation(film)
        );

        assertEquals("Название фильма не может быть путым!", emptyName.getMessage());
    }

    @Test
    public void checkLengthDescription() {
        Film film = new Film(1, "Film");
        String description = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Fugit earum aut deleniti!" +
                " Cupiditate nisi nobis possimus quia, facere beatae sequi, laborum ullam, quos maxime facilis " +
                "aliquid molestiae. Labore illum possimus corrupti non consectetur, saepe consequuntur quaerat" +
                " veritatis nemo quo deleniti ducimus dolore ea fugiat expedita sunt ipsum culpa, delectus eos, " +
                "commodi tenetur? Itaque facilis aut commodi dolores eius hic neque quibusdam unde incidunt " +
                "molestiae sint recusandae obcaecati culpa voluptatem, doloremque voluptatibus est, assumenda " +
                "quasi, suscipit quo? Iste autem animi cumque accusamus! Nostrum adipisci minus dignissimos, " +
                "iure itaque accusantium blanditiis saepe, sint sequi non provident? Adipisci voluptatem " +
                "corrupti, iusto officiis est natus ex nihil ipsam magnam suscipit ea deleniti, assumenda " +
                "quo quas harum voluptates magni laboriosam odio inventore tempora temporibus placeat vitae" +
                " aliquid eaque? Soluta deleniti incidunt, ipsa alias quidem voluptas impedit, iusto dicta" +
                " repellendus dolore sequi similique quam officiis inventore? Autem, temporibus magni alias" +
                " doloribus nihil modi, itaque dolorum est inventore commodi omnis maiores pariatur delectus" +
                " obcaecati ut ipsam qui iusto quos. Voluptatibus, in reprehenderit distinctio porro laborum" +
                " placeat aliquid dolores cum architecto itaque a nulla voluptas illo autem quidem adipisci" +
                " aut rem soluta asperiores, amet dolor blanditiis! Ex eum beatae aspernatur accusantium " +
                "praesentium provident nihil ipsam quam. Magni quas consectetur ratione delectus aperiam " +
                "deleniti eligendi fuga beatae, sequi mollitia.";

        film.setDescription(description);

        ValidationFilmException incorrectDescription = assertThrows(
                ValidationFilmException.class,
                () -> FilmValidation.validation(film)
        );

        assertEquals("Описание превышает 200 символов!", incorrectDescription.getMessage());
    }

    @Test
    public void checkDateRealize() {
        Film film = new Film(1, "Film");
        film.setReleaseDate(LocalDate.of(1800, Month.DECEMBER, 12));

        ValidationFilmException incorrectDateRealize = assertThrows(
                ValidationFilmException.class,
                () -> FilmValidation.validation(film)
        );

        assertEquals("Неверная дата выхода фильма в прокат!", incorrectDateRealize.getMessage());
    }

    @Test
    public void checkDuration() {
        Film film = new Film(1, "Film");
        film.setDuration(Duration.ofMinutes(-1000));

        ValidationFilmException incorrectDateRealize = assertThrows(
                ValidationFilmException.class,
                () -> FilmValidation.validation(film)
        );

        assertEquals("Неверная продолжительность фильма!", incorrectDateRealize.getMessage());
    }
}

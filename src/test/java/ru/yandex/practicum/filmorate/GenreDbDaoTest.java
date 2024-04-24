package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbDaoTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindAllGenres() {
        GenreDaoImpl genreDao = new GenreDaoImpl(jdbcTemplate);

        List<Genre> genreList = List.of(
                Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build(),
                Genre.builder().id(3).name("Мультфильм").build(),
                Genre.builder().id(4).name("Триллер").build(),
                Genre.builder().id(5).name("Документальный").build(),
                Genre.builder().id(6).name("Боевик").build()
        );

        List<Genre> testGenreList = genreDao.findAllGenre();

        assertEquals(genreList, testGenreList, "Разная длина списков");
        assertEquals(genreList.get(0), testGenreList.get(0), "Разные жанры");
    }

    @Test
    public void testFindGenreById() {
        GenreDaoImpl genreDao = new GenreDaoImpl(jdbcTemplate);
        Genre genre = Genre.builder().id(1).name("Комедия").build();
        Genre testGenre = genreDao.findGenreById(genre.getId());

        assertEquals(genre, testGenre, "Разные жанры");
    }
}

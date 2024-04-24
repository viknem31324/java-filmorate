package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;

import java.time.LocalDate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbDaoTest {
    private final JdbcTemplate jdbcTemplate;

    private final Genre genre = Genre.builder()
            .id(1)
            .name("Комедия")
            .build();

    private final Mpa mpa = Mpa.builder()
            .id(1)
            .name("G")
            .build();

    private final Film newFilm1 = Film.builder()
            .name("film name 1")
            .description("description")
            .releaseDate(LocalDate.of(1990, 11, 10))
            .duration(60)
            .genres(List.of(genre))
            .mpa(mpa)
            .build();

    private final Film newFilm2 = Film.builder()
            .name("film name 2")
            .description("description")
            .releaseDate(LocalDate.of(1995, 11, 10))
            .duration(90)
            .genres(List.of(genre))
            .mpa(mpa)
            .build();

    @Test
    public void testFindAllFilms() {
        FilmDaoImpl filmDao = new FilmDaoImpl(jdbcTemplate);
        GenreDaoImpl genreDao = new GenreDaoImpl(jdbcTemplate);
        MpaDaoImpl mpaDao = new MpaDaoImpl(jdbcTemplate);

        Film createdFilm = filmDao.createFilm(newFilm1);
        Film dbFilm = newFilm1.toBuilder().id(createdFilm.getId()).build();
        List<Film> filmsList = List.of(dbFilm);

        List<Film> films = filmDao.findAllFilms().stream()
                .map(item -> {
                    List<Genre> genres = genreDao.findAllGenreByFilmId(item.getId());
                    long mpaId = item.getMpa().getId();
                    Mpa mpa = mpaDao.findMpaById(mpaId);
                    return item.toBuilder().mpa(mpa).genres(genres).build();
                }).collect(Collectors.toList());;

        assertEquals(filmsList.size(), films.size(), "Некорректная длина списка");
        assertEquals(dbFilm, films.get(0), "Фильмы не равны");
    }

    @Test
    public void testFindFilmById() {
        FilmDaoImpl filmDao = new FilmDaoImpl(jdbcTemplate);
        GenreDaoImpl genreDao = new GenreDaoImpl(jdbcTemplate);
        MpaDaoImpl mpaDao = new MpaDaoImpl(jdbcTemplate);

        Film createdFilm = filmDao.createFilm(newFilm1);
        Mpa mpa = mpaDao.findMpaById(createdFilm.getMpa().getId());
        List<Genre> genres = genreDao.findAllGenreByFilmId(createdFilm.getId());
        Film dbCreatedFilm = createdFilm.toBuilder()
                .mpa(mpa)
                .genres(genres)
                .build();
        Film dbFilm = newFilm1.toBuilder()
                .id(createdFilm.getId())
                .build();

        assertEquals(dbFilm, dbCreatedFilm, "Фильмы не равны");
    }

    @Test
    public void testCreateAndUpdateUser() {
        FilmDaoImpl filmDao = new FilmDaoImpl(jdbcTemplate);
        GenreDaoImpl genreDao = new GenreDaoImpl(jdbcTemplate);
        MpaDaoImpl mpaDao = new MpaDaoImpl(jdbcTemplate);

        Film createdFilm1 = filmDao.createFilm(newFilm1);
        Mpa mpa1 = mpaDao.findMpaById(createdFilm1.getMpa().getId());
        List<Genre> genres1 = genreDao.findAllGenreByFilmId(createdFilm1.getId());
        Film dbCreatedFilm1 = createdFilm1.toBuilder()
                .mpa(mpa1)
                .genres(genres1)
                .build();
        Film dbFilm = newFilm1.toBuilder().id(createdFilm1.getId()).build();

        assertEquals(dbFilm, dbCreatedFilm1, "Фильмы не равны");

        Film updatedFilm = Film.builder()
                .id(dbFilm.getId())
                .name("new film name")
                .description("new description")
                .releaseDate(LocalDate.of(1990, 11, 10))
                .duration(90)
                .genres(List.of(genre))
                .mpa(mpa)
                .build();

        Film createdFilm2 = filmDao.updateFilm(updatedFilm);
        Mpa mpa2 = mpaDao.findMpaById(createdFilm2.getMpa().getId());
        List<Genre> genres2 = genreDao.findAllGenreByFilmId(createdFilm2.getId());
        Film dbCreatedFilm2 = createdFilm2.toBuilder()
                .mpa(mpa2)
                .genres(genres2)
                .build();

        assertEquals(updatedFilm, dbCreatedFilm2, "Фильмы не равны");
    }

    @Test
    public void testAddAndDeleteToLikes() {
        UserDaoImpl userDao = new UserDaoImpl(jdbcTemplate);

        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUser = userDao.createUser(newUser);
        User dbUser = newUser.toBuilder().id(createdUser.getId()).build();

        FilmDaoImpl filmDao = new FilmDaoImpl(jdbcTemplate);

        Film createdFilm1 = filmDao.createFilm(newFilm1);
        Film dbFilm = newFilm1.toBuilder().id(createdFilm1.getId()).build();
        Film testFilm1 = filmDao.addToLikes(dbFilm.getId(), dbUser.getId());

        assertEquals(1, testFilm1.getRate(), "Количество лайков не совпадает");

        Film testFilm2 = filmDao.deleteFromLikes(dbFilm.getId(), dbUser.getId());

        assertEquals(0, testFilm2.getRate(), "Количество лайков не совпадает");
    }

    @Test
    public void testFindPopularFilms() {
        UserDaoImpl userDao = new UserDaoImpl(jdbcTemplate);

        User newUser1 = User.builder()
                .email("user1@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUser1 = userDao.createUser(newUser1);
        User dbUser1 = newUser1.toBuilder().id(createdUser1.getId()).build();

        User newUser2 = User.builder()
                .email("user2@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUser2 = userDao.createUser(newUser2);
        User dbUser2 = newUser2.toBuilder().id(createdUser2.getId()).build();

        FilmDaoImpl filmDao = new FilmDaoImpl(jdbcTemplate);

        Film createdFilm1 = filmDao.createFilm(newFilm1);
        Film dbFilm1 = newFilm1.toBuilder().id(createdFilm1.getId()).build();
        filmDao.addToLikes(dbFilm1.getId(), dbUser1.getId());
        Film testFilm1 = filmDao.addToLikes(dbFilm1.getId(), dbUser2.getId());

        Film createdFilm2 = filmDao.createFilm(newFilm2);
        Film dbFilm2 = newFilm2.toBuilder().id(createdFilm2.getId()).build();
        Film testFilm2 = filmDao.addToLikes(dbFilm2.getId(), dbUser1.getId());

        List<Film> testPopularFilms = List.of(testFilm1, testFilm2);
        List<Film> popularFilms = filmDao.findPopularFilms(5);

        assertEquals(testPopularFilms.size(), popularFilms.size(), "Разная длина списков");
        assertEquals(testFilm1, popularFilms.get(0), "Неверная сортировака популярных фильмов");
    }
}

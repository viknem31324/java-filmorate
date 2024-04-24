package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmDao filmDao;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;

    @Autowired
    public FilmService(FilmDao filmDao, MpaDao mpaDao, GenreDao genreDao) {
        this.filmDao = filmDao;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    public List<Film> findAllFilms() {
        return filmDao.findAllFilms().stream()
                .map(item -> {
                    List<Genre> genres = genreDao.findAllGenreByFilmId(item.getId());
                    long mpaId = item.getMpa().getId();
                    Mpa mpa = mpaDao.findMpaById(mpaId);
                    return item.toBuilder().mpa(mpa).genres(genres).build();
                }).collect(Collectors.toList());
    }

    public Film findFilmById(int filmId) {
        Film film = filmDao.findFilmById(filmId);
        Mpa mpa = mpaDao.findMpaById(film.getMpa().getId());
        List<Genre> genres = genreDao.findAllGenreByFilmId(filmId);
        return film.toBuilder().mpa(mpa).genres(genres).build();
    }

    public Film createFilm(Film film) {
        FilmValidation.validation(film);

        Film createdFilm = filmDao.createFilm(film);
        Mpa mpa = mpaDao.findMpaById(createdFilm.getMpa().getId());
        List<Genre> genres = genreDao.findAllGenreByFilmId(createdFilm.getId());
        return createdFilm.toBuilder().mpa(mpa).genres(genres).build();
    }

    public Film updateFilm(Film film) {
        FilmValidation.validation(film);

        Film updatedFilm = filmDao.updateFilm(film);
        Mpa mpa = mpaDao.findMpaById(updatedFilm.getMpa().getId());
        List<Genre> genres = genreDao.findAllGenreByFilmId(updatedFilm.getId());
        return updatedFilm.toBuilder().mpa(mpa).genres(genres).build();
    }

    public Film addToLikes(long filmId, long userId) {
        Film film = filmDao.addToLikes(filmId, userId);
        Mpa mpa = mpaDao.findMpaById(film.getMpa().getId());
        List<Genre> genres = genreDao.findAllGenreByFilmId(filmId);
        return film.toBuilder().mpa(mpa).genres(genres).build();
    }

    public Film deleteFromLikes(long filmId, long userId) {
        Film film = filmDao.deleteFromLikes(filmId, userId);
        Mpa mpa = mpaDao.findMpaById(film.getMpa().getId());
        List<Genre> genres = genreDao.findAllGenreByFilmId(filmId);
        return film.toBuilder().mpa(mpa).genres(genres).build();
    }

    public List<Film> findPopularFilms(int count) {
        return filmDao.findPopularFilms(count).stream()
                .map(item -> {
                    long mpaId = item.getMpa().getId();
                    Mpa mpa = mpaDao.findMpaById(mpaId);
                    List<Genre> genres = genreDao.findAllGenreByFilmId(item.getId());
                    return item.toBuilder().mpa(mpa).genres(genres).build();
                }).collect(Collectors.toList());
    }
}

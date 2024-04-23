package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.List;

@Service
public class FilmService {
    private final FilmDao filmDao;

    @Autowired
    public FilmService(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    public List<Film> findAllFilms() {
        return filmDao.findAllFilms();
    }

    public Film findFilmById(int filmId) {
        return filmDao.findFilmById(filmId);
    }

    public Film createFilm(Film film) {
        FilmValidation.validation(film);

        return filmDao.createFilm(film);
    }

    public Film updateFilm(Film film) {
        FilmValidation.validation(film);

        return filmDao.updateFilm(film);
    }

    public Film addToLikes(long filmId, long userId) {
        return filmDao.addToLikes(filmId, userId);
    }

    public Film deleteFromLikes(long filmId, long userId) {
        return filmDao.deleteFromLikes(filmId, userId);
    }

    public List<Film> findPopularFilms(int count) {
        return filmDao.findPopularFilms(count);
    }
}

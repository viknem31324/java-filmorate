package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    List<Film> findAllFilms();

    Film findFilmById(long filmId);

    Film createFilm(Film requestFilm);

    Film updateFilm(Film film);

    Film addToLikes(long filmId, long userId);

    Film deleteFromLikes(long filmId, long userId);

    List<Film> findPopularFilms(int count);
}

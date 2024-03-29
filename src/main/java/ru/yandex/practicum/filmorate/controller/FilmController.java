package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.yandex.practicum.filmorate.exception.ParamsIncorrectException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film findFilmById(@PathVariable int filmId) {
        return filmService.findFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count <= 0) {
            throw new ParamsIncorrectException("Некорректный параметр count");
        }

        return filmService.findPopularFilms(count);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addToLikes(@PathVariable long id, @PathVariable long userId) {
        return filmService.addToLikes(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteFromLikes(@PathVariable long id, @PathVariable long userId) {
        return filmService.deleteFromLikes(id, userId);
    }
}

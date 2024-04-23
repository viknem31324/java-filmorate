package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
public class Film {
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    int rate;
    List<Genre> genres;
    Mpa mpa;
}

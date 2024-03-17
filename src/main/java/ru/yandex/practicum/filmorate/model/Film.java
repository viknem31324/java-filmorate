package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class Film {
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
}

package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private final int id;
    private final String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
}

package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    private final int id;
    @NonNull
    private final String email;
    @NonNull
    private final String login;
    private String name;
    private LocalDate birthday;
}

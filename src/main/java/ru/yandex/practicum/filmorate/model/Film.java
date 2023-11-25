package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public final class Film {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым!")
    private String name;
    @NotBlank(message = "Описание фильма не может быть пустым!")
    private String description;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private String releaseDate;
    @Positive(message = "Продолжительность фильма может быть только положительным числом")
    private int duration;
}

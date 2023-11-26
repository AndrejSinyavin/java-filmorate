package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import service.StartYear;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import static ru.yandex.practicum.filmorate.model.Properties.MAX_DESCRIPTION_LENGTH;

@Data
@Validated
public final class Film {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым!")
    private String name;
    //@NotBlank(message = "Описание фильма не может быть пустым!")
    @Size(max = MAX_DESCRIPTION_LENGTH,
            message = "Описание фильма не должно быть больше " + MAX_DESCRIPTION_LENGTH + " символов!"
    )
    private String description;
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    @NotBlank(message = "Дата релиза не может быть пустой!")
    @StartYear
    private String releaseDate;
    @Positive(message = "Продолжительность фильма может быть только положительным числом!")
    private int duration;
}

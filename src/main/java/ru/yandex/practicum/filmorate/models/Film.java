package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validations.Release;

import javax.validation.constraints.*;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.misc.ApplicationSettings.MAX_DESCRIPTION_LENGTH;

/**
 * Класс описывает поля записи "фильм" в фильмотеке
 */
@Data
public class Film {
    @DecimalMin(value = "0", message = "ID записи не может быть отрицательным значением")
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotNull(message = "Описание фильма не должно быть null")
    @Size(max = MAX_DESCRIPTION_LENGTH,
            message = "Описание фильма не должно быть больше " + MAX_DESCRIPTION_LENGTH + " символов")
    private String description;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Release
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма может быть только положительным значением")
    private int duration;

    @DecimalMin(value = "0", message = "Рейтинг фильма не может быть отрицательным значением")
    private int rate;
}

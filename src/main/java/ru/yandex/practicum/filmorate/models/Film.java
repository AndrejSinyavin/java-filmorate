package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.services.validation.Release;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import static ru.yandex.practicum.filmorate.services.misc.ValidateSettings.MAX_DESCRIPTION_LENGTH;

/**
 * Класс описывает поля записи "фильм" в фильмотеке
 */
@Data
@Validated
public final class Film {
    @DecimalMin(value = "0", message = "ID записи не может быть отрицательным значением")
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = MAX_DESCRIPTION_LENGTH,
            message = "Описание фильма не должно быть больше " + MAX_DESCRIPTION_LENGTH + " символов")
    private String description;

    @DateTimeFormat(pattern = "yyyy.MM.dd")
    @Release
    private String releaseDate;

    @Positive(message = "Продолжительность фильма может быть только положительным значением")
    private int duration;

    private int rate;
}

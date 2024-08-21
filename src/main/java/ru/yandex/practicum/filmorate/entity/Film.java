package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validate.Release;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.*;

/**
 * Класс описывает поля записи "фильм" в фильмотеке
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    @DecimalMin(value = "0", message = "ID записи не может быть отрицательным значением")
    int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    String name;

    @NotBlank(message = "Описание фильма не должно быть null")
    @Size(max = MAX_DESCRIPTION_LENGTH,
            message = "Описание фильма не должно быть больше " + MAX_DESCRIPTION_LENGTH + " символов")
    String description;

    @NotNull(message = "Поле даты не должно быть null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Release
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма может быть только положительным значением")
    int duration;

    double rate;
    Mpa mpa;
    List<Genre> genres;
    Set<Director> directors;
}

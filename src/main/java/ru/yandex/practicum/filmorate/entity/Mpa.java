package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * MPA-рейтинг фильма <a href="https://ru.wikipedia.org/wiki/Система_рейтингов_Американской_киноассоциации">
 */
@Data
@AllArgsConstructor
public class Mpa {
    @Positive(message = "ID MPA-рейтинга не может быть отрицательным значением")
    @NotNull(message = "ID MPA-рейтинга не может быть NULL")
    private int id;

    @NotNull(message = "ID MPA-рейтинга не может быть NULL")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
}

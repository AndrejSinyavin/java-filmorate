package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * MPA-рейтинг фильма <a href="https://ru.wikipedia.org/wiki/Система_рейтингов_Американской_киноассоциации">
 */
@Data
public class Mpa {
    @Positive(message = "ID MPA-рейтинга не может быть отрицательным значением")
    private int id;

    @NotBlank(message = "Название MPA-рейтинга не может быть пустым")
    private String name;
}

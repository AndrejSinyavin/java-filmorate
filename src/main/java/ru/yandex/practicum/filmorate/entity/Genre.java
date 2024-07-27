package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Жанр фильма
 */
@Data
public class Genre {
    @Positive(message = "ID жанра не может быть отрицательным значением")
    private int id;

    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;

    public int compareTo(Genre o2) {
        return this.id - o2.id;
    }
}

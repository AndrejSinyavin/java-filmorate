package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Жанр фильма
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre {
    @Positive(message = "ID жанра не может быть отрицательным значением")
    int id;

    @NotBlank(message = "Название жанра не может быть пустым")
    String name;

    public int compareTo(Genre o2) {
        return this.id - o2.id;
    }
}

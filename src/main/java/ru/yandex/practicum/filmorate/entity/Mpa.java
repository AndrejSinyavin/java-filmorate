package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * MPA-рейтинг фильма <a href="https://ru.wikipedia.org/wiki/Система_рейтингов_Американской_киноассоциации">
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Mpa {
    @Positive(message = "ID MPA-рейтинга не может быть отрицательным значением")
    int id;

    @NotBlank(message = "Название MPA-рейтинга не может быть пустым")
    String name;

    public int compareTo(Mpa o2) {
        return this.id - o2.id;
    }
}

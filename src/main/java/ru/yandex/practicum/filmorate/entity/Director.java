package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Режиссер фильма
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Director {
    @Positive(message = "ID режиссера не может быть отрицательным значением")
    int id;

    @NotBlank(message = "ФИО режиссера не может быть пустым")
    String name;

    public int compareTo(Director o2) {
        return this.id - o2.id;
    }
}

package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.DecimalMin;
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
    @DecimalMin(value = "0", message = "ID режиссера не может быть отрицательным значением")
    int id;

    String name;

    public int compareTo(Director o2) {
        return this.id - o2.id;
    }
}

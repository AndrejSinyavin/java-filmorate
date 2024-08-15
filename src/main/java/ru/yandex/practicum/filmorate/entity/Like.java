package ru.yandex.practicum.filmorate.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Класс описывает поля записи "лайк"
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Like {
    int userId;
    int filmId;
}

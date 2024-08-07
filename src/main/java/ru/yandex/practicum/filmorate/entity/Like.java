package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс описывает поля записи "лайк"
 */
@Data
@AllArgsConstructor
public class Like {
    private int userId;
    private int filmId;
}

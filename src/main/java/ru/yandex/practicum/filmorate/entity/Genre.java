package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Жанр фильма
 */
@Data
@AllArgsConstructor
public class Genre {
    private int id;
    private String name;
}

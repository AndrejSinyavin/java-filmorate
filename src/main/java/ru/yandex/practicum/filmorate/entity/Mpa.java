package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * MPA-рейтинг фильма <a href="https://ru.wikipedia.org/wiki/Система_рейтингов_Американской_киноассоциации">
 */
@Data
@AllArgsConstructor
public class Mpa {
    private int id;
    private String name;
}

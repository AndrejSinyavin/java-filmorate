package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.entity.Genre;

import java.util.List;

public interface BaseGenreService {
    /**
     * Метод возвращает ID жанра и его имя
     * @param id искомый жанр
     * @return ID жанра и его имя
     */
    Genre getGenre(int id);

    /**
     * Метод возвращает список из ID жанра и его имени для всех жанров
     * @return список из ID жанра и его имени для всех жанров
     */
    List<Genre> getAllGenres();
}

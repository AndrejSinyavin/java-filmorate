package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;

import java.util.List;

public interface UtilRepository {

    /**
     * Метод получает из репозитория список всех имеющихся жанров для фильмов
     *
     * @return список всех имеющихся жанров
     */
    List<Genre> getAllGenres();

    /**
     * Метод возвращает ID жанра и его имя из репозитория
     *
     * @param id искомый жанр
     * @return ID жанра и его имя
     */
    Genre getGenreById(int id);

    /**
     * Метод возвращает MPA-рейтинг по его ID из репозитория
     *
     * @param id MPA-рейтинг
     * @return {@link Mpa}
     */
    Mpa getMpaById(int id);

    /**
     * Метод возвращает все MPA-рейтинги из репозитория
     *
     * @return список из {@link Mpa}
     */
    List<Mpa> getAllMpa();
}

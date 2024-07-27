package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public interface BaseUtilityService {
    /**
     * Метод проверяет, что пользователи существуют в репозитории.
     *
     * @param firstUserId  первый пользователь
     * @param secondUserId второй пользователь
     */
    void validateUserIds(int firstUserId, int secondUserId);

    /**
     * Метод проверяет, что фильмы существуют в репозитории.
     *
     * @param firstFilmId  первый фильм
     * @param secondFilmId второй фильм
     */
    void validateFilmIds(int firstFilmId, int secondFilmId);

    /**
     * Сервисный метод выполняет валидацию ID жанра фильма по данным из репозитория и возвращает DTO ID-жанра, имя жанра
     *
     * @param mpaId ID жанра фильма
     * @return DTO {@link Mpa}
     */
    Mpa validateMpaIdAndGetMpaFromDb(int mpaId);

    Set<Genre> validateGenreIdAndGetGenreNames(Film film);

    TreeSet<Genre> getFilmGenresFromDb(int filmId);

    List<Genre> getGenresFromDb();

    Genre getGenre(int genreId);
}

package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;

public interface BaseFilmService {
    /**
     * Метод позволяет пользователю лайкнуть фильм.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    void addLike(int filmId, int userId);

    /**
     * Метод позволяет пользователю удалить ранее поставленный лайк фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    void deleteLike(int filmId, int userId);

    /**
     * Метод получает топ лучших фильмов по лайкам пользователей.
     *
     * @param topSize размер топа
     * @return топ лучших фильмов
     */
    List<Film> getTopFilms(int topSize);

    /**
     * Метод создает запись о фильме на сервисе.
     *
     * @param film фильм
     * @return этот же фильм с установленным ID после регистрации
     */
    Film createfilm(Film film);

    /**
     * Метод обновляет запись о фильме на сервисе.
     *
     * @param film обновленная запись о фильме
     * @return обновленная запись о фильме
     */
    Film updateFilm(Film film);

    /**
     * Метод возвращает список всех записей о фильмах.
     *
     * @return список фильмов
     */
    List<Film> getFilms();

    /**
     * Метод возвращает запись о конкретном фильме.
     *
     * @param id ID искомого фильма
     * @return найденная запись о фильме
     */
    Film getFilm(int id);
}

package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Like;

import java.util.List;

/**
 * Интерфейс для служб, работающих с рейтингом фильмов.
 */
public interface RatingRepository {

    /**
     * Пользователь ставит лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     */
    void likeFilm(int filmId, int userId);

    /**
     * Пользователь отменяет лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     */
    void dislikeFilm(int filmId, int userId);

    void updateFilmRate(int filmId);

    /**
     * Метод возвращает список всех записей о лайках фильмам.
     *
     * @return список лайков, может быть пустым
     */
    List<Like> getLikes();

    /**
     * Метод возвращает true, если у пользователя есть хотя бы 1 лайк
     *
     * @return true или false
     */
    Boolean isUserHasLikes(int userId);

}
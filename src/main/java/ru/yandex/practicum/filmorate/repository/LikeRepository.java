package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Like;

import java.util.List;

/**
 * Интерфейс для служб, работающих с рейтингом фильмов.
 */
public interface LikeRepository {

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
    void unLikeFilm(int filmId, int userId);

    /**
     * Метод возвращает рейтинг фильма
     *
     * @param filmId ID фильма
     * @return пустое значение, если операция завершена успешно, иначе сформированное исключение
     */
    int getFilmRate(int filmId);

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
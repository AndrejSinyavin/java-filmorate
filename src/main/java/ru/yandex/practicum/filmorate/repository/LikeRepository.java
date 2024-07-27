package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Film;

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
    void undoLikeFilm(int filmId, int userId);

    /**
     * Метод возвращает рейтинг фильма
     *
     * @param filmId ID фильма
     * @return пустое значение, если операция завершена успешно, иначе сформированное исключение
     */
    int getFilmRate(int filmId);

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @param topSize размер топа
     * @return список ID фильмов топа в порядке убывания количества лайков
     */
    List<Film> getPopularFilm(int topSize);

}
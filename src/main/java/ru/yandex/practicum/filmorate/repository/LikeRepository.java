package ru.yandex.practicum.filmorate.repository;

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

}
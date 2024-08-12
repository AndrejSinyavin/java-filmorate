package ru.yandex.practicum.filmorate.repository;

/**
 * Интерфейс для реализации работы с лайками/дилайками отзывов
 */
public interface ReviewLikeRepository {
    /**
     * Добавление лайка пользователя для отзыва
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    void addLike(Integer reviewId, Integer userId);

    /**
     * Добавление дизлайка пользователя для отзыва
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    void addDislike(Integer reviewId, Integer userId);

    /**
     * Удаление лайка пользователя для отзыва
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    void deleteLike(Integer reviewId, Integer userId);

    /**
     * Удаление дизлайка пользователя для отзыва
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     */
    void deleteDislike(Integer reviewId, Integer userId);
}

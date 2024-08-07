package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.entity.Review;

import java.util.Collection;

public interface BaseReviewService {

    /**
     * Получение коллекции отзывов, отсортированных по рейтингу
     *
     * @param filmId идентификатор фильма
     * @param count  количество отзывов (по умолчанию 10)
     * @return возвращает коллекцию объектов Review
     */
    Collection<Review> get(Integer filmId, Integer count);

    /**
     * Получение отзыва по идендификатору
     *
     * @param reviewId идентификатор отзыва
     * @return возвращает объект Review
     */
    Review getById(Integer reviewId);

    /**
     * Создание отзыва
     *
     * @param review объект Review
     * @return возвращает созданный объект Review
     */
    Review create(Review review);

    /**
     * Обновление отзыва
     *
     * @param review объект Review
     * @return возвращает обновленный объект Review
     */
    Review update(Review review);

    /**
     * Удаление отзыва
     *
     * @param reviewId идентификатор отзыва
     */
    void delete(Integer reviewId);

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

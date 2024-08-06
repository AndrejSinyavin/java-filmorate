package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Review;

import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс для реализации работы с отзывами
 */
public interface ReviewRepository {

    /**
     * Получение коллекции отзывов, отсортированных по рейтингу
     *
     * @param filmId идентификатор фильма
     * @param count количество отзывов (по умолчанию 10)
     * @return возвращает коллекцию объектов Review
     */
    Collection<Review> get(Integer filmId, Integer count);

    /**
     * Получение отзыва по идендификатору
     *
     * @param reviewId идентификатор отзыва
     * @return возвращает объект Review
     */
    Optional<Review> getById(Integer reviewId);

    /**
     * Создание отзыва
     *
     * @param review объект Review
     * @return возвращает созданный объект Review
     */
    Optional<Review> create(Review review);

    /**
     * Обновление отзыва
     *
     * @param review объект Review
     * @return возвращает обновленный объект Review
     */
    Optional<Review> update(Review review);

    /**
     * Удаление отзыва
     *
     * @param reviewId идентификатор отзыва
     */
    void delete(Integer reviewId);
}

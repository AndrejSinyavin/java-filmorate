package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.service.BaseReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    public final BaseReviewService reviewService;

    /**
     * Endpoint получения первых count отзывов по идентификатору фильма либо первых count всех отзывов
     *
     * @param filmId идентификатор фильма
     * @param count количество отзывов (по умолчанию 10)
     * @return возвращает коллекцию объектов Review
     */
    @GetMapping
    public Collection<Review> get(
            @RequestParam(value = "filmId", required = false) Integer filmId,
            @RequestParam(value = "count", defaultValue = "10") Integer count
    ) {
        log.info("{}: GET отзывы по ид фильма = {}, count = {}", getClass().getSimpleName(), filmId, count);
        return reviewService.get(filmId, count);
    }

    /**
     * Endpoint получения отзыва по идентификатору
     *
     * @param reviewId идентификатор отзыва
     * @return возвращает объект Review
     */
    @GetMapping("/{id}")
    public Review getById(@PathVariable("id") int reviewId) {
        log.info("{}: GET отзыва по идентификатору id = {}", getClass().getSimpleName(), reviewId);
        return reviewService.getById(reviewId);
    }

    /**
     * Endpoint добавления нового отзыва
     *
     * @param review объект Review
     * @return возвращает созданный объект Review
     */
    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("{}: POST добавление отзыва {}", getClass().getSimpleName(), review.toString());
        return reviewService.create(review);
    }

    /**
     * Endpoint обновления отзыва
     *
     * @param review объект Review
     * @return возвращает обновленный объект Review
     */
    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("{}: PUT обновление отзыва {}", getClass().getSimpleName(), review.toString());
        return reviewService.update(review);
    }

    /**
     * Endpoint удаления отзыва по идентификатору
     *
     * @param reviewId идентификатор отзыва
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int reviewId) {
        log.info("{}: DELETE отзыва по идентификатору id = {}", getClass().getSimpleName(), reviewId);
        reviewService.delete(reviewId);
    }

    /**
     * Endpoint добавления лайка для отзыва
     *
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/like/{user-id}")
    public void addLike(@PathVariable("id") int reviewId, @PathVariable("user-id") int userId) {
        log.info("{}: PUT лайка по идентификатору отзыва {} и идентификатору пользователя {}",
                getClass().getSimpleName(),
                reviewId,
                userId);
        reviewService.addLike(reviewId, userId);
    }

    /**
     * Endpoint добавления дизлайка для отзыва
     *
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/dislike/{user-id}")
    public void addDislike(@PathVariable("id") int reviewId, @PathVariable("user-id") int userId) {
        log.info("{}: PUT дизлайка по идентификатору отзыва {} и идентификатору пользователя {}",
                getClass().getSimpleName(),
                reviewId,
                userId);
        reviewService.addDislike(reviewId, userId);
    }

    /**
     * Endpoint удаления лайка отзыва
     *
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{user-id}")
    public void deleteLike(@PathVariable("id") int reviewId, @PathVariable("user-id") int userId) {
        log.info("{}: DELETE лайка по идентификатору отзыва {} и идентификатору пользователя {}",
                getClass().getSimpleName(),
                reviewId,
                userId);
        reviewService.deleteLike(reviewId, userId);
    }

    /**
     * Endpoint удаления дизлайка отзыва
     *
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/dislike/{user-id}")
    public void deleteDislike(@PathVariable("id") int reviewId, @PathVariable("user-id") int userId) {
        log.info("{}: DELETE дизлайка по идентификатору отзыва {} и идентификатору пользователя {}",
                getClass().getSimpleName(),
                reviewId,
                userId);
        reviewService.deleteDislike(reviewId, userId);
    }
}

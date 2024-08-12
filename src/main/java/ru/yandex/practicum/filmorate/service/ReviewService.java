package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.repository.ReviewLikeRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService implements BaseReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Override
    public Collection<Review> get(Integer filmId, Integer count) {
        log.info("{}: Получение первых {} всех отзывов / отзывов по идентификатору фильма {}",
                getClass().getSimpleName(),
                count,
                filmId
        );
        return reviewRepository.get(filmId, count);
    }

    @Override
    public Review getById(Integer reviewId) {
        log.info("{}: Получение отзывов по идентификатору {}", getClass().getSimpleName(), reviewId);
        return reviewRepository.getById(reviewId).orElseThrow(
                () -> new EntityNotFoundException(
                        getClass().getSimpleName(),
                        "",
                        String.format("Отзыв с ид %s не найден", reviewId))
        );
    }

    @Override
    public Review create(Review review) {
        log.info("{}: Добавление отзыва {} ", getClass().getSimpleName(), review.toString());
        return reviewRepository.create(review).orElseThrow(
                () -> new InternalServiceException(getClass().getSimpleName(), "", "Ошибка добавления отзыва")
        );
    }

    @Override
    public Review update(Review review) {
        log.info("{}: Обновление отзыва {} ", getClass().getSimpleName(), review.toString());
        return reviewRepository.update(review).orElseThrow(
                () -> new EntityNotFoundException(
                        getClass().getSimpleName(),
                        "",
                        String.format("Отзыв с ид %s не найден", review.getReviewId()))
        );
    }

    @Override
    public void delete(Integer reviewId) {
        log.info("{}: Удаление отзыва по идентификатору {}", getClass().getSimpleName(), reviewId);
        reviewRepository.delete(reviewId);
    }

    @Override
    public void addLike(Integer reviewId, Integer userId) {
        log.info("{}: Добавление лайка для отзыва с идентификатором {} пользователем {}", getClass().getSimpleName(), reviewId, userId);
        reviewLikeRepository.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        log.info("{}: Добавление дизлайка для отзыва с идентификатором {} пользователем {}", getClass().getSimpleName(), reviewId, userId);
        reviewLikeRepository.addDislike(reviewId, userId);
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        log.info("{}: Удаление лайка для отзыва с идентификатором {} пользователем {}", getClass().getSimpleName(), reviewId, userId);
        reviewLikeRepository.deleteLike(reviewId, userId);
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        log.info("{}: Удаление дизлайка для отзыва с идентификатором {} пользователем {}", getClass().getSimpleName(), reviewId, userId);
        reviewLikeRepository.deleteDislike(reviewId, userId);
    }
}

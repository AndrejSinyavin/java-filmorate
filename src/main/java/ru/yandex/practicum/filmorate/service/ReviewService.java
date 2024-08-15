package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.EventOperation;
import ru.yandex.practicum.filmorate.entity.EventType;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.repository.*;

import java.time.Instant;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService implements BaseReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final EventRepository eventRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

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
        if (!isFilmExist(review.getFilmId()) || !isUserExist(review.getUserId())) {
            throw new EntityNotFoundException(
                    getClass().getSimpleName(),
                    "",
                    "Пользователь/Фильм не найден"
            );
        }
        Review resultReview = reviewRepository.create(review).orElseThrow(
                () -> new InternalServiceException(getClass().getSimpleName(), "", "Ошибка добавления отзыва")
        );
        eventRepository.create(new Event(Instant.now().toEpochMilli(), review.getUserId(), EventType.REVIEW.toString(), EventOperation.ADD.toString(),
                review.getReviewId()));
        return resultReview;
    }

    @Override
    public Review update(Review review) {
        log.info("{}: Обновление отзыва {} ", getClass().getSimpleName(), review.toString());
        Review resultReview = reviewRepository.update(review).orElseThrow(
                () -> new EntityNotFoundException(
                        getClass().getSimpleName(),
                        "",
                        String.format("Отзыв с ид %s не найден", review.getReviewId()))
        );
        eventRepository.create(new Event(Instant.now().toEpochMilli(), review.getUserId(), EventType.REVIEW.toString(), EventOperation.UPDATE.toString(),
                review.getReviewId()));
        return resultReview;
    }

    @Override
    public void delete(Integer reviewId) {
        int userId = getById(reviewId).getUserId();
        log.info("{}: Удаление отзыва по идентификатору {}", getClass().getSimpleName(), reviewId);
        reviewRepository.delete(reviewId);
        eventRepository.create(new Event(Instant.now().toEpochMilli(), userId, EventType.REVIEW.toString(),
                EventOperation.REMOVE.toString(),
                reviewId));
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

    private boolean isFilmExist(Integer filmId) {
        return filmRepository.getFilm(filmId).isPresent();
    }

    private boolean isUserExist(Integer userId) {
        return userRepository.getUser(userId).isPresent();
    }
}

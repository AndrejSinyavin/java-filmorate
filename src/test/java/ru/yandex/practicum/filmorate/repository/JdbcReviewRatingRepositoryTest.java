package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.repository.mappers.ReviewRowMapper;

import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@Import({JdbcReviewRepository.class, JdbcReviewLikeRepository.class, ReviewRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Класс тестов для JdbcReviewLikeRepository")
class JdbcReviewRatingRepositoryTest {
    private final JdbcReviewRepository reviewRepository;
    private final JdbcReviewLikeRepository reviewLikeRepository;

    @Test
    @DisplayName("Добавление лайка для отзыва")
    void shouldAddLike() {
        Review review = reviewRepository.create(getTestReviewForFilmID1()).orElse(null);
        assertNotNull(review);
        reviewLikeRepository.addLike(review.getReviewId(), 1);
        Review resultReview = reviewRepository.getById(review.getReviewId()).orElse(null);
        assertNotNull(resultReview);
        assertEquals(1, resultReview.getUseful(), "лайк не добавлен");
    }

    @Test
    @DisplayName("Добавление дизлайка для отзыва")
    void shouldAddDislike() {
        Review review = reviewRepository.create(getTestReviewForFilmID1()).orElse(null);
        assertNotNull(review);
        reviewLikeRepository.addDislike(review.getReviewId(), 1);
        Review resultReview = reviewRepository.getById(review.getReviewId()).orElse(null);
        assertNotNull(resultReview);
        assertEquals(-1, resultReview.getUseful(), "лайк не добавлен");
    }

    @Test
    @DisplayName("Удаление лайка отзыва")
    void shouldDeleteLike() {
        Review review = reviewRepository.create(getTestReviewForFilmID1()).orElse(null);
        assertNotNull(review);
        reviewLikeRepository.addLike(review.getReviewId(), 1);
        reviewLikeRepository.deleteLike(review.getReviewId(), 1);
        Review resultReview = reviewRepository.getById(review.getReviewId()).orElse(null);
        assertNotNull(resultReview);
        assertEquals(0, resultReview.getUseful(), "лайк не удален");
    }

    @Test
    @DisplayName("Удаление дизлайка отзыва")
    void shouldDeleteDislike() {
        Review review = reviewRepository.create(getTestReviewForFilmID1()).orElse(null);
        assertNotNull(review);
        reviewLikeRepository.addDislike(review.getReviewId(), 1);
        reviewLikeRepository.deleteDislike(review.getReviewId(), 1);
        Review resultReview = reviewRepository.getById(review.getReviewId()).orElse(null);
        assertNotNull(resultReview);
        assertEquals(0, resultReview.getUseful(), "лайк не удален");
    }

    private static Review getTestReviewForFilmID1() {
        Review review = new Review();
        review.setContent("Положительный отзыв");
        review.setFilmId(1);
        review.setUserId(1);
        review.setIsPositive(Boolean.TRUE);
        return  review;
    }
}
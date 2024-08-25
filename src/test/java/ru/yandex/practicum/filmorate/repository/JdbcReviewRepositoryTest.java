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

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@Import({JdbcReviewRepository.class, ReviewRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Класс тестов для JdbcReviewRepository")
class JdbcReviewRepositoryTest {
    private final JdbcReviewRepository reviewRepository;

    @Test
    @DisplayName("Получение всех отзывов")
    void shouldReturnTwoReviews() {
        reviewRepository.create(getTestReviewForFilmID1());
        Collection<Review> resultReviews =  reviewRepository.get(null, 10);
        assertNotNull(resultReviews);
        assertEquals(1, resultReviews.size(), "Неверное общее количество отзывов");
    }

    @Test
    @DisplayName("Получение первых трех отзывов по фильму")
    void shouldReturnThreeReviews() {
        reviewRepository.create(getTestReviewForFilmID1());
        reviewRepository.create(getTestReviewForFilmID2());
        reviewRepository.create(getTestReviewForFilmID2());
        reviewRepository.create(getTestReviewForFilmID2());
        reviewRepository.create(getTestReviewForFilmID2());
        Collection<Review> resultReviews =  reviewRepository.get(2, 3);
        assertNotNull(resultReviews);
        assertEquals(3, resultReviews.size(), "Неверное общее количество отзывов");
    }

    @Test
    @DisplayName("Получение отзыва по идентификатору")
    void shouldReturnReviewById() {
        Review sourceReview = getTestReviewForFilmID1();
        Review review = reviewRepository.create(sourceReview).orElse(null);
        assertNotNull(review);

        Review resultReview = reviewRepository.getById(review.getReviewId()).orElse(null);
        assertNotNull(resultReview);
        assertEquals(sourceReview.getContent(), resultReview.getContent(), "Неверное содержание отзыва");
        assertEquals(sourceReview.getFilmId(), resultReview.getFilmId(), "Неверный идентификатор фильма");
        assertEquals(sourceReview.getUserId(), resultReview.getUserId(), "Неверный идентификатор пользователя");
        assertEquals(sourceReview.getIsPositive(), resultReview.getIsPositive(), "Неверный тип отзыва");
    }

    @Test
    @DisplayName("Добавление отзыва")
    void shouldAddReview() {
        Review sourceReview = getTestReviewForFilmID1();
        Review review = reviewRepository.create(sourceReview).orElse(null);
        assertNotNull(review);

        Review resultReview = reviewRepository.getById(review.getReviewId()).orElse(null);
        assertNotNull(resultReview);
    }

    @Test
    @DisplayName("Обновление отзыва")
    void shouldUpdateReview() {
        Review sourceReview = getTestReviewForFilmID1();
        Review review = reviewRepository.create(sourceReview).orElse(null);
        assertNotNull(review);
        review.setContent("негативный отзыв");
        review.setIsPositive(Boolean.FALSE);

        Review resultReview = reviewRepository.update(review).orElse(null);
        assertNotNull(resultReview);
        assertEquals(review.getContent(), resultReview.getContent(), "Неверное содержание отзыва");
        assertEquals(review.getIsPositive(), resultReview.getIsPositive(), "Неверный тип отзыва");
    }

    @Test
    @DisplayName("Удаление отзыва")
    void shouldDeleteReview() {
        Review review = reviewRepository.create(getTestReviewForFilmID1()).orElse(null);
        assertNotNull(review);
        reviewRepository.delete(review.getReviewId());
        assertEquals(Optional.empty(),
                reviewRepository.getById(review.getReviewId()),
                "Отзыв не удален");
    }

    private static Review getTestReviewForFilmID1() {
        Review review = new Review();
        review.setContent("Положительный отзыв");
        review.setFilmId(1);
        review.setUserId(1);
        review.setIsPositive(Boolean.TRUE);
        return  review;
    }

    private static Review getTestReviewForFilmID2() {
        Review review = new Review();
        review.setContent("отзыв");
        review.setFilmId(2);
        review.setUserId(1);
        review.setIsPositive(Boolean.TRUE);
        return  review;
    }
}
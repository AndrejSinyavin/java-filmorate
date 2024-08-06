package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcReviewLikeRepository implements ReviewLikeRepository {
    private final NamedParameterJdbcOperations jdbc;

    private static final String INSERT_QUERY = "MERGE INTO review_like (review_id, user_id, liked) " +
            "KEY(review_id, user_id) VALUES(:review_id, :user_id, :liked)";

    private static final String DELETE_QUERY = "DELETE FROM review_like WHERE review_id = :review_id " +
            "and user_id = :user_id and liked = :liked";

    @Override
    public void addLike(Integer reviewId, Integer userId) {
        log.trace("{}: call addLike() with parameters reviewId = {}, userId = {}", getClass().getSimpleName(), reviewId, userId);
        addReviewLike(reviewId, userId, Boolean.TRUE);
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        log.trace("{}: call addDislike() with parameters reviewId = {}, userId = {}", getClass().getSimpleName(), reviewId, userId);
        addReviewLike(reviewId, userId, Boolean.FALSE);
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        log.trace("{}: call deleteLike() with parameters reviewId = {}, userId = {}", getClass().getSimpleName(), reviewId, userId);
        deleteReviewLike(reviewId, userId, Boolean.TRUE);
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        log.trace("{}: call deleteDislike() with parameters reviewId = {}, userId = {}", getClass().getSimpleName(), reviewId, userId);
        deleteReviewLike(reviewId, userId, Boolean.FALSE);
    }

    private void addReviewLike(Integer reviewId, Integer userId, Boolean liked) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "review_id", reviewId,
                "user_id", userId,
                "liked", liked
        ));
        log.trace("{}: MapSqlParameterSource = {}", getClass().getSimpleName(), params);
        try {
            jdbc.update(INSERT_QUERY, params);
        } catch (DataAccessException e) {
            log.error("{}: throw EntityNotFoundException", getClass().getSimpleName());
            throw new EntityNotFoundException(
                    getClass().getSimpleName(),
                    e.getClass().getName(),
                    String.format("Отзыва %d и/или пользователя %d не найдено", reviewId, userId)
            );
        }
    }

    private void deleteReviewLike(Integer reviewId, Integer userId, Boolean liked) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "review_id", reviewId,
                "user_id", userId,
                "liked", liked
        ));
        jdbc.update(DELETE_QUERY, params);
    }
}

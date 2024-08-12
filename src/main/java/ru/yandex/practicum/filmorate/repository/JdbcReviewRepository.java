package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.repository.mappers.ReviewRowMapper;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {
    private static final String FIND_FIRST_BY_FILM_QUERY = "SELECT rw.*, nvl(rl.useful, 0) AS useful " +
            "FROM review rw " +
            "LEFT JOIN (SELECT rl.review_id, NVL(SUM(CASEWHEN(liked, 1, 0)) - SUM(CASEWHEN(NOT liked, 1, 0)),0) AS useful " +
            "             FROM review_like rl GROUP BY rl.review_id) rl ON rl.review_id = rw.REVIEW_ID " +
            "WHERE rw.film_id = :film_id OR :film_id IS null " +
            "ORDER BY rl.useful DESC NULLS LAST " +
            "LIMIT :count";
    private static final String FIND_BY_ID_QUERY = "SELECT rw.*, " +
            "(SELECT SUM(CASEWHEN(liked, 1, 0)) - SUM(CASEWHEN(NOT liked, 1, 0)) " +
            "   FROM review_like rl WHERE rl.review_id = rw.review_id) AS useful " +
            "FROM review rw WHERE rw.review_id = :review_id";
    private static final String INSERT_QUERY = "INSERT INTO review (content, positive, user_id, film_id)" +
            "VALUES (:content, :positive, :user_id, :film_id)";
    private static final String UPDATE_QUERY = "UPDATE review SET content = :content, positive = :positive, " +
            "user_id = :user_id, film_id = :film_id WHERE review_id = :review_id";
    private static final String DELETE_QUERY = "DELETE FROM review WHERE review_id = :review_id";
    private final NamedParameterJdbcOperations jdbc;
    private final ReviewRowMapper mapper;

    @Override
    public Collection<Review> get(Integer filmId, Integer count) {
        log.trace("{}: call get({}, {})", getClass().getSimpleName(), filmId, count);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("count", count);
        log.trace("{}: MapSqlParameterSource = {}", getClass().getSimpleName(), params);
        return jdbc.query(FIND_FIRST_BY_FILM_QUERY, params, mapper);
    }

    @Override
    public Optional<Review> getById(Integer reviewId) {
        log.trace("{}: call getById({})", getClass().getSimpleName(), reviewId);
        MapSqlParameterSource params = new MapSqlParameterSource(
                Map.of("review_id", reviewId)
        );
        log.trace("{}: MapSqlParameterSource = {}", getClass().getSimpleName(), params);
        try {
            Review result = jdbc.queryForObject(FIND_BY_ID_QUERY, params, mapper);
            log.trace("{}: call jdbc.queryForObject Review = {}", getClass().getSimpleName(), result);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            log.error("{}: EmptyResultDataAccessException called, empty to return", getClass().getSimpleName());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Review> create(Review review) {
        log.trace("{}: call create() with parameter review = {}", getClass().getSimpleName(), review.toString());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "content", review.getContent(),
                "positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId()

        ));
        log.trace("{}: MapSqlParameterSource = {}", getClass().getSimpleName(), params);
        try {
            jdbc.update(INSERT_QUERY, params, keyHolder);
            Integer id = keyHolder.getKeyAs(Integer.class);
            if (id == null) {
                log.error("{}: id is null", getClass().getSimpleName());
                throw new InternalServerException(
                        getClass().getSimpleName(),
                        InternalServerException.class.getSimpleName(),
                        "Не удалось обновить данные"
                );
            }
            review.setReviewId(id);
            log.trace("{}: created {}", getClass().getSimpleName(), review);
            return Optional.of(review);
        } catch (DataAccessException e) {
            log.error("{}: throw EntityNotFoundException", getClass().getSimpleName());
            throw new EntityNotFoundException(
                    getClass().getSimpleName(),
                    e.getClass().getName(),
                    String.format("Пользователя %d и/или фильма %d не найдено", review.getUserId(), review.getFilmId())
            );
        }
    }

    @Override
    public Optional<Review> update(Review review) {
        log.trace("{}: call update() with parameter review = {}", getClass().getSimpleName(), review.toString());
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "review_id", review.getReviewId(),
                "content", review.getContent(),
                "positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId()

        ));
        log.trace("{}: MapSqlParameterSource = {}", getClass().getSimpleName(), params);
        try {
            int rowsUpdated = jdbc.update(UPDATE_QUERY, params);
            if (rowsUpdated == 0) {
                log.error("{}: 0 rows updated", getClass().getSimpleName());
                throw new EntityNotFoundException(
                        getClass().getSimpleName(),
                        EntityNotFoundException.class.getSimpleName(),
                        String.format("Отзыв с ид %s не найден", review.getReviewId()));
            }
        } catch (DataAccessException e) {
            log.error("{}: throw InternalServerException", getClass().getSimpleName());
            throw new InternalServerException(
                    getClass().getSimpleName(),
                    e.getClass().getSimpleName(),
                    "Не удалось обновить данные"
            );
        }

        return Optional.of(review);
    }

    @Override
    public void delete(Integer reviewId) {
        log.trace("{}: call delete() with parameter reviewId = {}", getClass().getSimpleName(), reviewId);
        MapSqlParameterSource params = new MapSqlParameterSource(
                Map.of("review_id", reviewId)
        );
        log.trace("{}: MapSqlParameterSource = {}", getClass().getSimpleName(), params);
        jdbc.update(DELETE_QUERY, params);
    }
}

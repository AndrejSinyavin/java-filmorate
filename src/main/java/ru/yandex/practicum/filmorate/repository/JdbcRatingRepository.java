package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Like;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.RATING_SCALE_DIMENSION;

/**
 * Репозиторий для работы с рейтингами фильмов в БД
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcRatingRepository implements RatingRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final String thisService = this.getClass().getName();

    /**
     * Пользователь ставит 'лайк' фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    @Override
    public void likeFilm(int filmId, int userId) {
        log.info("Пользователь ID {} ставит 'лайк' фильму ID {}", userId, filmId);
        setLike(filmId, userId, true);
        updateFilmRate(filmId);
        log.info("Лайк добавлен в БД");
    }

    /**
     * Пользователь ставит 'дизлайк' фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    @Override
    public void dislikeFilm(int filmId, int userId) {
        log.info("Пользователь ID {} ставит 'дизлайк' фильму ID {}", userId, filmId);
        setLike(filmId, userId, false);
        updateFilmRate(filmId);
        log.info("Дизлайк добавлен в БД");
    }

    private void setLike(int filmId, int userId, boolean like) {
        try {
            String sqlQuery =
                    "MERGE INTO FILMS_RATINGS (FR_FILM_ID_PK , FR_USER_ID_PK, FR_RATING) " +
                    "VALUES (:filmId, :userId, :like)";
            jdbc.update(sqlQuery, new MapSqlParameterSource()
                    .addValue("filmId", filmId)
                    .addValue("userId", userId)
                    .addValue("like", like));
        } catch (DataIntegrityViolationException e) {
            String warn = "Фильма и/или пользователя с указанными ID не существует";
            log.warn(warn);
            throw new EntityNotFoundException(thisService, e.getClass().getName(), warn);
        }
    }

    /**
     * Метод рассчитывает и обновляет рейтинг фильма среди пользователей.
     *
     * @param filmId ID фильма
     */
    @Override
    public void updateFilmRate(int filmId) {
        log.info("Обновление рейтинга фильма в БД");
        String sqlQuery = """
                MERGE INTO FILMS (FILM_ID_PK, FILM_RATING)
                VALUES (:filmId,
                    (SELECT CAST(SUM(FR_RATING) AS DEC(7,2)) / COUNT(*) * :scale
                    FROM FILMS_RATINGS
                    WHERE FR_FILM_ID_PK = :filmId)
                )""";
        int numRows;
        try {
            numRows = jdbc.update(sqlQuery, Map.of("filmId", filmId, "scale", RATING_SCALE_DIMENSION));
        } catch (DataIntegrityViolationException e) {
            log.warn("Фильм не имеет лайков от пользователей");
            return;
        }
        if (numRows != 1) {
            String error = "Ошибка! SQL-запрос обновил более одной записи, или ни одной";
            log.error(error);
            throw new InternalServiceException(thisService, jdbc.getClass().getName(), error);
        } else {
            log.info("Обновлен рейтинг у фильма с ID {}", filmId);
        }
    }

    /**
     * Метод получения всех лайков всех пользователей
     *
     * @return список объектов лайк с данными таблицы FILMS_RATINGS
     */
    @Override
    public List<Like> getLikes() {
        log.info("Получение информации о всех лайках из БД");
        String sqlQuery = """
                SELECT FR_USER_ID_PK, FR_FILM_ID_PK
                from FILMS_RATINGS
                where FR_RATING IS TRUE""";
        return jdbc.query(sqlQuery, likeMapper());
    }

    /**
     * Метод возвращает истину, если пользователь поставил хотя бы 1 лайк
     *
     * @param userId ID пользователя
     * @return true или false
     */
    @Override
    public Boolean isUserHasLikes(int userId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", userId);
        return jdbc.queryForObject("""
                    select exists (select FR_USER_ID_PK
                                   from FILMS_RATINGS
                                   where FR_USER_ID_PK = :id and (FR_RATING is TRUE))""",
                params, Boolean.class);
    }

    private RowMapper<Like> likeMapper() {
        return (ResultSet rs, int rowNum) -> new Like(
                rs.getInt("FR_USER_ID_PK"),
                rs.getInt("FR_FILM_ID_PK"));
    }
}

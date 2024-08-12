package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Like;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcLikeRepository implements LikeRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final DataSource jdbcTemplate;
    private final String thisService = this.getClass().getName();
    private final String idError = "Ошибка! ID пользователя может быть только положительным значением";

    /**
     * Пользователь ставит лайк фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    @Override
    public void likeFilm(@Positive(message = idError) int filmId,
                         @Positive(message = idError) int userId) {
        log.info("Пользователь ID {} ставит лайк фильму ID {}", userId, filmId);
        SimpleJdbcInsertOperations simpleJdbc = new SimpleJdbcInsert(jdbcTemplate);
        try {
            simpleJdbc.withTableName("FILMS_RATINGS")
                    .execute(Map.of("FR_FILM_ID_PK", filmId, "FR_USER_ID_PK", userId));
            log.info("Лайк добавлен в БД");
        } catch (DuplicateKeyException e) {
            String info = "Лайк уже был добавлен в БД";
            log.warn(info);
            throw new EntityAlreadyExistsException(thisService, e.getClass().getName(), info);
        } catch (DataAccessException e) {
            String warn = String.format("Пользователя %d и/или фильма %d не найдено", userId, filmId);
            log.warn(warn);
            throw new EntityNotFoundException(thisService, e.getClass().getName(), warn);
        }

    }

    /**
     * Пользователь отменяет лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     */
    @Override
    public void unLikeFilm(@Positive(message = idError) int filmId,
                           @Positive(message = idError) int userId) {
        log.info("Пользователь ID {} отменяет лайк фильму ID {}", userId, filmId);
        Map<String, Object> parameters = Map.of(
                "filmId", filmId,
                "userId", userId);
        String sqlQuery = "delete from FILMS_RATINGS where FR_FILM_ID_PK = :filmId and FR_USER_ID_PK = :userId";
        if (jdbc.update(sqlQuery, parameters) == 0) {
            String likeAdded = "Запись о лайке не найдена в БД";
            log.warn(likeAdded);
            throw new EntityNotFoundException(thisService, jdbc.getClass().getName(), likeAdded);
        } else {
            log.info("Лайк удален из БД");
        }
    }

    /**
     * Метод получения рейтинга фильма среди пользователей.
     *
     * @param filmId ID фильма
     * @return количество пользователей, проголосовавших за этот фильм
     */
    public int getFilmRate(int filmId) {
        log.info("Получение рейтинга фильма из БД");
        String sqlQuery = """
                select count(FR_USER_ID_PK)
                from FILMS_RATINGS
                where FR_FILM_ID_PK = :filmId""";
        var filmRating = jdbc.queryForObject(sqlQuery, Map.of("filmId", filmId), Integer.class);
        if (filmRating == null || filmRating < 0) {
            String error = "Ошибка! SQL-запрос вернул NULL или отрицательное значение, " +
                    "маппинг поиска рейтинга фильма выполнен некорректно";
            log.error(error);
            throw new InternalServiceException(thisService, jdbc.getClass().getName(), error);
        } else {
            log.info("Фильм ID {} имеет {} лайк(а)", filmId, filmRating);
            return filmRating;
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
                select *
                from FILMS_RATINGS""";
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
        return jdbc.queryForObject("select exists(select from FILMS_RATINGS where FR_USER_ID_PK = :id)",
                params, Boolean.class);
    }

    private RowMapper<Like> likeMapper() {
        return (ResultSet rs, int rowNum) -> new Like(
                rs.getInt("FR_USER_ID_PK"),
                rs.getInt("FR_FILM_ID_PK"));
    }
}

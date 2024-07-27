package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.service.BaseUtilityService;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcLikeRepository implements LikeRepository {
    private final BaseUtilityService checkDb;
    private final NamedParameterJdbcOperations jdbc;
    private final DataSource source;
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
        checkDb.validateUserIds(userId, userId);
        checkDb.validateFilmIds(filmId, filmId);
        SimpleJdbcInsert simpleJdbc = new SimpleJdbcInsert(source);
        Map<String, Object> parameters = Map.of(
                "FR_FILM_ID_PK", filmId,
                "FR_USER_ID_PK", userId);
        try {
            simpleJdbc.withTableName("USERS")
                    .usingGeneratedKeyColumns("FILMS_RATING_PK")
                    .execute(parameters);
        } catch (DuplicateKeyException e) {
            String likeAdded = "Пользователь уже добавил 'лайк' фильму";
            log.warn(likeAdded, e);
            throw new EntityAlreadyExistsException(thisService, e.getClass().getName(), likeAdded);
        }

    }

    /**
     * Пользователь отменяет лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     */
    @Override
    public void undoLikeFilm(@Positive(message = idError) int filmId,
                             @Positive(message = idError) int userId) {
        log.info("Пользователь ID {} отменяет лайк фильму ID {}", userId, filmId);
        checkDb.validateUserIds(userId, userId);
        checkDb.validateFilmIds(filmId, filmId);
        Map<String, Object> parameters = Map.of(
                "FR_FILM_ID_PK", filmId,
                "FR_USER_ID_PK", userId);
        String sqlQuery = "delete from FILMS_RATINGS where FR_FILM_ID_PK = :filmId and FR_USER_ID_PK = :userId";
        if (jdbc.update(sqlQuery, parameters) != 0) {
            String likeAdded = "Запись не найдена";
            log.warn(likeAdded);
            throw new EntityNotFoundException(thisService, jdbc.getClass().getName(), likeAdded);
        }
    }

    /**
     * Метод получения рейтинга фильма среди пользователей. Зарезервирован для будущего использования
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
            return filmRating;
        }
    }

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @param topSize размер топа
     * @return список ID фильмов топа в порядке убывания количества лайков
     */
    @Override
    public List<Film> getPopularFilm(int topSize) {
        log.info("Получение топа рейтинга фильмов из БД, размер топа: {}", topSize);
        String sqlQuery = """
                          select *, (select count(FR_USER_ID_PK)
                                      from FILMS_RATINGS
                                      where FR_FILM_ID_PK = FILM_ID_PK) as RATE
                          from FILMS
                          order by RATE
                          limit :topSize""";
        return jdbc.query(sqlQuery, Map.of("topSize", topSize), (rs, rowNum) ->
                new Film(
                        rs.getInt("FILM_ID_PK"),
                        rs.getString("FILM_NAME"),
                        rs.getString("FILM_DESCRIPTION"),
                        rs.getDate("FILM_RELEASE_DATE").toLocalDate(),
                        rs.getInt("FILM_DURATION"),
                        rs.getInt("RATE"),
                        checkDb.validateMpaIdAndGetMpaFromDb(rs.getInt("FILM_MPA_RATING_FK")),
                        checkDb.getFilmGenresFromDb(rs.getInt("FILM_ID_PK"))
                ));
    }
}

package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier
@RequiredArgsConstructor
@Valid
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final DataSource source;
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность Film = null";
    private final String dbError = "Сбой в работе СУБД";

    /**
     * Метод создает запись о фильме в БД.
     *
     * @param film запись о фильме, которую нужно создать в БД
     * @return этот же фильм с уже зарегистрированным ID в БД
     */
    @Override
    public Optional<Film> createfilm(@NotNull(message = entityNullError) Film film) {
        log.info("Создание записи о фильме в БД:");
        SimpleJdbcInsert simpleJdbc = new SimpleJdbcInsert(source);
        int mpaId = film.getMpa().getId();
        var mpa = selectMpaFromDb(mpaId);
        Map<String, Object> parameters =
                Map.of("FILM_NAME", film.getName(),
                        "FILM_DESCRIPTION", film.getDescription(),
                        "FILM_RELEASE_DATE", film.getReleaseDate(),
                        "FILM_DURATION", film.getDuration(),
                        "FILM_MPA_RATING_FK", mpaId);
        simpleJdbc.withTableName("FILMS").usingGeneratedKeyColumns("FILM_ID_PK");
        try {
            int newID = (int) simpleJdbc.executeAndReturnKey(parameters);
            if (newID <= 0) {
                String error = "БД вернула для фильма некорректный ID " + newID;
                log.error(error);
                throw new InternalServiceException(thisService, this.getClass().getName(), error);
            } else {
                film.setId(newID);
                film.setMpa(mpa);
                return Optional.of(film);
            }
        } catch (DataAccessException e) {
            log.error(dbError, e);
            throw new InternalServiceException(thisService, e.getClass().getName(), dbError);
        }
    }

    /**
     * Метод обновляет существующую запись о фильме в БД.
     *
     * @param film фильм с обновленными полями.
     * @return обновленная запись о фильме
     */
    @Override
    public Optional<Film> updateFilm(Film film) {
        log.info("Обновление записи о фильме в БД:");
        String sqlQuery = """
                          delete from FRIENDSHIP_STATUSES
                          where FS_USER_ID = :firstUserId AND FS_FRIEND_ID = :secondUserId""";
        int mpaId = film.getMpa().getId();
        var mpa = selectMpaFromDb(mpaId);
        var paramSource = new MapSqlParameterSource()
                .addValue("FILM_NAME", film.getName())
                .addValue("FILM_DESCRIPTION", film.getDescription())
                .addValue("FILM_RELEASE_DATE", film.getReleaseDate())
                .addValue("FILM_DURATION", film.getDuration())
                .addValue("FILM_MPA_RATING_FK", mpaId);
        try {
            var updated = jdbc.update(sqlQuery, paramSource);
            if (updated == 0) {
                log.error(entityNullError);
                return Optional.empty();
            } else {
                return Optional.of(film);
            }
        } catch (DataAccessException e) {
            log.error(dbError, e);
            throw new InternalServiceException(thisService, e.getClass().getName(), dbError);
        }
    }

    /**
     * Метод возвращает список всех записей о фильмах в фильмотеке.
     *
     * @return список фильмов, может быть пустым
     */
    @Override
    public List<Film> getFilms() {
        return List.of();
    }

    /**
     * Метод возвращает запись о фильме по его ID.
     *
     * @param filmId ID искомого фильма
     * @return запись о фильме; либо пустое значение, если запись о фильме не найдена в хранилище
     */
    @Override
    public Optional<Film> getFilm(int filmId) {
        log.info("Чтение записи о фильме из БД:");
        String sqlQuery = "select * from FILMS where FILM_ID_PK = :filmId";
        var paramSource = new MapSqlParameterSource().addValue("filmId", filmId);
        var film = jdbc.queryForObject(sqlQuery, paramSource, (rs, rowNum) ->
                new Film(
                        rs.getInt("FILM_ID_PK"),
                        rs.getString("FILM_NAME"),
                        rs.getString("FILM_DESCRIPTION"),
                        rs.getDate("FILM_RELEASE_DATE").toLocalDate(),
                        rs.getInt("FILM_DURATION"),
                        selectRateFromDb(filmId),
                        selectMpaFromDb(rs.getInt("FILM_MPA_RATING_FK"))
                ));
        return Optional.ofNullable(film);
    }

    private Mpa selectMpaFromDb(int mpaId) {
        log.info("Валидация и получение жанра по имеющимся данным в БД");
        String sqlQuery = "select MPA_RATING_NAME from MPA_RATINGS where MPA_RATING_ID_PK = :mpaId";
        try {
            var ratingName = jdbc.queryForObject(sqlQuery,
                    new MapSqlParameterSource().addValue("mpaId", mpaId),
                    String.class);
            if (ratingName == null) {
                log.warn("MPA рейтинг с ID {} не найден в БД", mpaId);
                throw new EntityNotFoundException(thisService, this.getClass().getName(),
                        "Указанный MPA рейтинг не найден в БД");
            } else {
                return new Mpa(mpaId, ratingName);
            }
        } catch (EmptyResultDataAccessException e) {
            String warn = "Жанра с таким ID в БД не найдено";
            log.warn(warn, e);
            throw new EntityNotFoundException(thisService, e.getClass().getName(), warn);
        } catch (DataAccessException e)  {
            String error = "Ошибка при работе с БД";
            log.error(error, e);
            throw new InternalServiceException(thisService, e.getClass().getName(), error);
        }
    }

    private int selectRateFromDb (int filmId) {
        log.info("Получение рейтинга фильма из БД");
        String sqlQuery = "select count(FR_USER_ID_PK) from FILMS_RATINGS where FR_FILM_ID_PK = :filmId";
        var film = new MapSqlParameterSource().addValue("filmId", filmId);
        return jdbc.queryForObject(sqlQuery, film, Integer.class);
    }
}

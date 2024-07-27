package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import javax.sql.DataSource;
import java.util.*;

@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final UtilRepository util;
    private final NamedParameterJdbcOperations jdbc;
    private final DataSource source;
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность Film = null";

    /**
     * Метод создает запись о фильме в БД.
     *
     * @param film запись о фильме, которую нужно создать в БД
     * @return этот же фильм с уже зарегистрированным ID в БД
     */
    @Override
    public Optional<Film> createFilm(@NotNull(message = entityNullError) Film film) {
        log.info("Создание записи о фильме в БД");
        SimpleJdbcInsert simpleJdbc = new SimpleJdbcInsert(source);
        int mpaId = film.getMpa().getId();
        var mpa = util.validateMpaIdAndGetMpaFromDb(mpaId);
        var genres = util.validateGenreIdAndGetGenreNames(film);
        Map<String, Object> parameters = Map.of(
                        "FILM_NAME", film.getName(),
                        "FILM_DESCRIPTION", film.getDescription(),
                        "FILM_RELEASE_DATE", film.getReleaseDate(),
                        "FILM_DURATION", film.getDuration(),
                        "FILM_MPA_RATING_FK", mpaId);
        simpleJdbc.withTableName("FILMS").usingGeneratedKeyColumns("FILM_ID_PK");
        int generatedID = simpleJdbc.executeAndReturnKey(parameters).intValue();
        if (generatedID <= 0) {
            String error = "Ошибка! БД вернула для фильма некорректный ID " + generatedID;
            log.error(error);
            throw new InternalServiceException(thisService, simpleJdbc.getClass().getName(), error);
        } else {
            film.setId(generatedID);
            film.setMpa(mpa);
            film.setGenres(genres);
            return Optional.of(film);
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
        log.info("Обновление записи о фильме в БД");
        int filmId = film.getId();
        int mpaId = film.getMpa().getId();
        var mpa = util.validateMpaIdAndGetMpaFromDb(mpaId);
        var genres = util.validateGenreIdAndGetGenreNames(film);
        String sqlQuery = """
                          update FILMS set
                          FILM_NAME = :name, FILM_DESCRIPTION = :description, FILM_RELEASE_DATE = :releaseDate,
                          FILM_DURATION = :duration, FILM_MPA_RATING_FK = :mpaId
                          where FILM_ID_PK = :filmId""";
        var paramSource = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", mpaId);
        var dbUpdatedRows = jdbc.update(sqlQuery, paramSource);
        if (dbUpdatedRows == 0) {
            log.warn("Запрос на обновление в БД не обновил ни одной записи!");
            return Optional.empty();
        } else if (dbUpdatedRows > 1) {
            String error = "Критическая ошибка! БД обновила больше одного фильма";
            log.error(error);
            throw new InternalServiceException(thisService, jdbc.getClass().getName(), error);
        } else {
            film.setMpa(mpa);
            film.setGenres(genres);
            return Optional.of(film);
        }
    }

    /**
     * Метод возвращает список всех фильмов из БД.
     *
     * @return список фильмов, может быть пустым
     */
    @Override
    public List<Film> getFilms() {
        log.info("Чтение всех записей о фильмах из БД");
        String sqlQuery = """
                          select *, (select count(FR_USER_ID_PK)
                                      from FILMS_RATINGS
                                      where FR_FILM_ID_PK = FILM_ID_PK) as RATE
                          from FILMS
                          order by FILM_ID_PK""";
        return jdbc.query(sqlQuery, (rs, rowNum) ->
                new Film(
                        rs.getInt("FILM_ID_PK"),
                        rs.getString("FILM_NAME"),
                        rs.getString("FILM_DESCRIPTION"),
                        rs.getDate("FILM_RELEASE_DATE").toLocalDate(),
                        rs.getInt("FILM_DURATION"),
                        rs.getInt("RATE"),
                        util.validateMpaIdAndGetMpaFromDb(rs.getInt("FILM_MPA_RATING_FK")),
                        util.getFilmGenresFromDb(rs.getInt("FILM_ID_PK"))
                ));
    }

    /**
     * Метод возвращает запись о фильме по его ID.
     *
     * @param filmId ID искомого фильма
     * @return запись о фильме; либо пустое значение, если запись о фильме не найдена в хранилище
     */
    @Override
    public Optional<Film> getFilm(int filmId) {
        log.info("Чтение записи о фильме из БД");
        String sqlQuery = """
                          select *, (select count(FR_USER_ID_PK)
                                      from FILMS_RATINGS
                                      where FR_FILM_ID_PK = :filmId) as RATE
                          from FILMS
                          where FILM_ID_PK = :filmId""";
        try {
            var film = jdbc.queryForObject(sqlQuery, Map.of("filmId", filmId), (rs, rowNum) ->
                    new Film(
                            rs.getInt("FILM_ID_PK"),
                            rs.getString("FILM_NAME"),
                            rs.getString("FILM_DESCRIPTION"),
                            rs.getDate("FILM_RELEASE_DATE").toLocalDate(),
                            rs.getInt("FILM_DURATION"),
                            rs.getInt("RATE"),
                            util.validateMpaIdAndGetMpaFromDb(rs.getInt("FILM_MPA_RATING_FK")),
                            util.getFilmGenresFromDb(rs.getInt("FILM_ID_PK"))
                    ));
            if (film == null) {
                String error =
                        "Ошибка! SQL-запрос вернул NULL, маппинг получения данных о пользователе выполнен некорректно!";
                log.error(error);
                throw new InternalServiceException(thisService, jdbc.getClass().getName(), error);
            }
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Фильм с ID {} не найден в БД", filmId);
            return Optional.empty();
        }
    }

}

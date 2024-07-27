package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import java.util.*;

@Log4j2
@Component
@Valid
@RequiredArgsConstructor
public class DateBaseUtilityService implements BaseUtilityService {
    private final NamedParameterJdbcTemplate jdbc;
    private final String thisService = this.getClass().getName();
    private final String errorSqlRequest =
            "Ошибка! SQL-запрос вернул NULL, возможно есть ошибка в структуре БД или запроса";

    /**
     * Сервисный метод проверяет, что пользователи существуют в БД.
     *
     * @param firstUserId  первый пользователь
     * @param secondUserId второй пользователь
     */
    @Override
    public void validateUserIds(int firstUserId, int secondUserId) {
        log.info("Проверка, что в БД есть записи о пользователях ID {} и ID {}", firstUserId, secondUserId);
        String sqlQuery = """
                select count(USER_ID_PK)
                from USERS
                where USER_ID_PK = :userOne or USER_ID_PK = :userTwo""";
        var paramSource = new MapSqlParameterSource()
                .addValue("userOne", firstUserId)
                .addValue("userTwo", secondUserId);
        Integer foundUsers = jdbc.queryForObject(sqlQuery, paramSource, Integer.class);
        if (foundUsers == null) {
            log.error(errorSqlRequest);
            throw new InternalServiceException(thisService, this.getClass().getName(), errorSqlRequest);
        } else if (foundUsers != 2) {
            throw new EntityNotFoundException(thisService, jdbc.getClass().getName(),
                    "Один или оба пользователя отсутствуют в БД");
        }
    }

    /**
     * Сервисный метод проверяет, что фильмы существуют в БД.
     *
     * @param firstFilmId  первый фильм
     * @param secondFilmId второй фильм
     */
    @Override
    public void validateFilmIds(int firstFilmId, int secondFilmId) {
        log.info("Проверка, что в БД есть записи о фильмах ID {} и ID {}", firstFilmId, secondFilmId);
        String sqlQuery = """
                select count(FILM_ID_PK)
                from FILMS
                where FILM_ID_PK = :filmOne or FILM_ID_PK = :filmTwo""";
        var paramSource = new MapSqlParameterSource()
                .addValue("filmOne", firstFilmId)
                .addValue("filmTwo", secondFilmId);
        Integer foundUsers = jdbc.queryForObject(sqlQuery, paramSource, Integer.class);
        if (foundUsers == null) {
            log.error(errorSqlRequest);
            throw new InternalServiceException(thisService, this.getClass().getName(), errorSqlRequest);
        } else if (foundUsers != 2) {
            throw new EntityNotFoundException(thisService, jdbc.getClass().getName(),
                    "Один или оба фильма отсутствуют в БД");
        }
    }

    /**
     * Сервисный метод выполняет валидацию MPA-рейтинга по данным из БД и возвращает корректный Mpa-рейтинг
     * @param mpaId ID MPA-рейтинга
     * @return корректный MPA-рейтинг
     */
    @Override
    public Mpa validateMpaIdAndGetMpaFromDb(int mpaId) {
        log.info("Валидация ID MPA-рейтинга и получение его названия из БД");
        String sqlQuery = """
                            select MPA_RATING_NAME
                            from MPA_RATINGS
                            where MPA_RATING_ID_PK = :mpaId""";
        try {
            var ratingName = jdbc.queryForObject(sqlQuery, Map.of("mpaId", mpaId), String.class);
            if (ratingName == null) {
                log.error(errorSqlRequest);
                throw new InternalServiceException(thisService, jdbc.getClass().getName(), errorSqlRequest);
            } else {
                return new Mpa(mpaId, ratingName);
            }
        } catch (EmptyResultDataAccessException e) {
            String warn = "Жанра с таким ID в БД не найдено";
            log.warn(warn, e);
            throw new EntityNotFoundException(thisService, e.getClass().getName(), warn);
        }
    }

    /**
     * Сервисный метод выполняет валидацию жанров фильма по содержимому в БД
     * и возвращает корректный список жанров с ID жанра и именем
     * @param film фильм с проверяемым списком жанров
     * @return корректный список жанров
     */
    @Override
    public Set<Genre> validateGenreIdAndGetGenreNames(Film film) {
        log.info("Валидация жанров фильма по содержимому в БД");
        var dbGenres = getGenresFromDb();
        int maxId = dbGenres.size();
        var filmGenres = film.getGenres();
        if (filmGenres == null) {
            return new TreeSet<>(Genre::compareTo);
        } else {
            for (var genre : filmGenres) {
                int id = genre.getId();
                if (id > maxId) {
                    log.warn(errorSqlRequest);
                    throw new EntityNotFoundException(thisService, "метод 'validateFilmGenres'", errorSqlRequest);
                } else {
                    genre.setName(dbGenres.get(id - 1).getName());
                }
            }
            return new HashSet<>(filmGenres);
        }
    }

    /**
     * Сервисный метод получает из БД полный список жанров с именами для фильма с индексом ID
     * @param filmId ID целевого фильма
     * @return корректный список жанров для этого фильма
     */
    @Override
    public TreeSet<Genre> getFilmGenresFromDb(int filmId) {
        log.info("Получение списка жанров фильма ID {} из БД", filmId);
        var dbGenres = getGenresFromDb();
        String sqlQuery = """
                            select distinct FG_GENRE_ID
                            from FILMS_GENRES
                            where FG_FILM_ID = :filmId
                            order by FG_GENRE_ID""";
        var listFilmsGenreIds = jdbc.queryForList(sqlQuery, Map.of("filmId", filmId), Integer.class);
        var genres = new TreeSet<>(Genre::compareTo);
        for (var genreId : listFilmsGenreIds) {
            genres.add(new Genre(genreId, dbGenres.get(genreId - 1).getName()));
        }
        return genres;
    }

    /**
     * Сервисный метод получает из БД список всех имеющихся жанров для фильмов
     * @return список всех имеющихся жанров
     */
    @Override
    public List<Genre> getGenresFromDb() {
        log.info("Получение списка всех имеющихся жанров в ДБ");
        String sqlQuery = "select * from GENRES order by GENRE_ID_PK";
        return jdbc.query(sqlQuery, (rs, rowNum) ->
                new Genre(
                        rs.getInt("GENRE_ID_PK"),
                        rs.getString("GENRE_NAME")
                ));
    }

    /**
     * Метод возвращает ID жанра и его имя из БД
     * @param genreId ID искомого жанра
     * @return ID и имя жанра
     */
    @Override
    public Genre getGenre(int genreId) {
        log.info("Получение названия жанра и его ID из БД");
        String sqlQuery = "select * from GENRES where GENRE_ID_PK = :genreId";
        var genre = jdbc.queryForObject(sqlQuery, Map.of("genreId", genreId), Genre.class);
        if (genre == null) {
            String error = "Ошибка! SQL-запрос вернул NULL, маппинг поиска жанра фильма выполнен некорректно";
            log.error(error);
            throw new InternalServiceException(thisService, jdbc.getClass().getName(), error);
        }
        return genre;
    }
}

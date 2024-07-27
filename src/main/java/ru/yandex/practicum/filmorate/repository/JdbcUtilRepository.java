package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import java.util.*;

@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcUtilRepository implements UtilRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final String thisService = this.getClass().getName();
    String errorSql = "Ошибка! SQL-запрос вернул NULL, маппинг поиска жанра фильма выполнен некорректно";

    /**
     * Метод получает из БД список всех имеющихся жанров для фильмов
     *
     * @return список всех имеющихся жанров {@link Genre}
     */
    @Override
    public List<Genre> getAllGenresFromDb() {
        log.info("Получение списка всех имеющихся жанров в БД");
        String sqlQuery = "select * from GENRES order by GENRE_ID_PK";
        return jdbc.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class));
    }

    /**
     * Метод получает из БД жанр по известному ID жанра
     *
     * @param genreId ID искомого жанра
     * @return {@link Genre}
     */
    @Override
    public Genre getGenreById(int genreId) {
        log.info("Получение названия жанра и его ID из БД");
        String sqlQuery = "select * from GENRES where GENRE_ID_PK = :genreId";
        var genre = jdbc.queryForObject(sqlQuery, Map.of("genreId", genreId), Genre.class);
        if (genre == null) {
            log.error(errorSql);
            throw new InternalServiceException(thisService, jdbc.getClass().getName(), errorSql);
        }
        return genre;
    }

    /**
     * Метод получает из БД MPA-рейтинг по известному ID рейтинга
     *
     * @param id ID искомого MPA-рейтинга
     * @return {@link Mpa}
     */
    @Override
    public Mpa getMpaById(int id) {
        // Todo
        return null;
    }

    /**
     * Метод получает из БД все MPA-рейтинги
     *
     * @return список всех имеющихся рейтингов {@link Mpa}
     */
    @Override
    public List<Mpa> getAllMpa() {
        // Todo
        return List.of();
    }


    /**
     * Метод проверяет, что пользователи существуют в БД. Метод также можно использовать и для одного пользователя.
     *
     * @param firstUserId  первый пользователь
     * @param secondUserId второй пользователь (или еще раз первый)
     */
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
            log.error(errorSql);
            throw new InternalServiceException(thisService, this.getClass().getName(), errorSql);
        } else if (foundUsers != 2) {
            throw new EntityNotFoundException(thisService, jdbc.getClass().getName(),
                    "Один или оба пользователя отсутствуют в БД");
        }
    }

    /**
     * Метод проверяет, что фильмы существуют в БД. Метод также можно использовать и для одного пользователя.
     *
     * @param firstFilmId  первый фильм
     * @param secondFilmId второй фильм (или еще раз первый)
     */
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
            log.error(errorSql);
            throw new InternalServiceException(thisService, this.getClass().getName(), errorSql);
        } else if (foundUsers != 2) {
            throw new EntityNotFoundException(thisService, jdbc.getClass().getName(),
                    "Один или оба фильма отсутствуют в БД");
        }
    }

    /**
     * Сервисный метод выполняет валидацию MPA-рейтинга по данным из БД и возвращает корректный Mpa-рейтинг
     *
     * @param mpaId ID MPA-рейтинга
     * @return корректный MPA-рейтинг
     */
    public Mpa validateMpaIdAndGetMpaFromDb(int mpaId) {
        log.info("Валидация ID MPA-рейтинга и получение его названия из БД");
        String sqlQuery = """
                select MPA_RATING_NAME
                from MPA_RATINGS
                where MPA_RATING_ID_PK = :mpaId""";
        try {
            var ratingName = jdbc.queryForObject(sqlQuery, Map.of("mpaId", mpaId), String.class);
            if (ratingName == null) {
                log.error(errorSql);
                throw new InternalServiceException(thisService, jdbc.getClass().getName(), errorSql);
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
     *
     * @param film фильм с проверяемым списком жанров
     * @return корректный список жанров
     */
    public Set<Genre> validateGenreIdAndGetGenreNames(Film film) {
        log.info("Валидация жанров фильма по содержимому в БД");
        var dbGenres = getAllGenresFromDb();
        int maxId = dbGenres.size();
        var filmGenres = film.getGenres();
        if (filmGenres == null) {
            return new TreeSet<>(Genre::compareTo);
        } else {
            for (var genre : filmGenres) {
                int id = genre.getId();
                if (id > maxId) {
                    log.warn(errorSql);
                    throw new EntityNotFoundException(thisService, "метод 'validateFilmGenres'", errorSql);
                } else {
                    genre.setName(dbGenres.get(id - 1).getName());
                }
            }
            return new HashSet<>(filmGenres);
        }
    }

    /**
     * Сервисный метод получает из БД полный список жанров с именами для фильма с индексом ID
     *
     * @param filmId ID целевого фильма
     * @return корректный список жанров для этого фильма
     */
    public TreeSet<Genre> getFilmGenresFromDb(int filmId) {
        log.info("Получение списка жанров фильма ID {} из БД", filmId);
        var dbGenres = getAllGenresFromDb();
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
}

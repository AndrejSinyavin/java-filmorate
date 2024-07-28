package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import java.sql.ResultSet;
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
}

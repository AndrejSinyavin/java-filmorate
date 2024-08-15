package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import java.util.List;
import java.util.Map;

@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcUtilRepository implements UtilRepository {
    final String errorSql = "Ошибка! SQL-запрос вернул NULL, маппинг поиска жанра фильма выполнен некорректно";
    private final NamedParameterJdbcOperations jdbc;
    private final String thisService = this.getClass().getName();

    /**
     * Метод получает из БД список всех имеющихся жанров для фильмов
     *
     * @return список всех имеющихся жанров {@link Genre}
     */
    @Override
    public List<Genre> getAllGenres() {
        log.info("Получение всех имеющихся жанров из БД");
        String sqlQuery = "select * from GENRES order by GENRE_ID_PK";
        return jdbc.query(sqlQuery, ((rs, rowNum) ->
                new Genre(rs.getInt("GENRE_ID_PK"), rs.getString("GENRE_NAME")
                )));
    }

    /**
     * Метод получает из БД жанр по известному ID жанра
     *
     * @param genreId ID искомого жанра
     * @return {@link Genre}
     */
    @Override
    public Genre getGenreById(int genreId) {
        log.info("Получение жанра из БД");
        String sqlQuery = "select GENRE_NAME from GENRES where GENRE_ID_PK = :genreId";
        try {
            var genreName = jdbc.queryForObject(sqlQuery, Map.of("genreId", genreId), String.class);
            if (genreName == null) {
                log.error(errorSql);
                throw new InternalServiceException(thisService, jdbc.getClass().getName(), errorSql);
            } else {
                return new Genre(genreId, genreName);
            }
        } catch (EmptyResultDataAccessException e) {
            var warn = String.format("Жанр с ID %d не найден в БД", genreId);
            log.warn(warn);
            throw new EntityNotFoundException(thisService, jdbc.getClass().getName(), warn);
        }
    }

    /**
     * Метод получает из БД MPA-рейтинг по известному ID рейтинга
     *
     * @param mpaId ID искомого MPA-рейтинга
     * @return {@link Mpa}
     */
    @Override
    public Mpa getMpaById(int mpaId) {
        log.info("Получение рейтинга MPA из БД");
        String sqlQuery = "select MPA_RATING_NAME from MPA_RATINGS where MPA_RATING_ID_PK = :mpaId";
        try {
            var mpaName = jdbc.queryForObject(sqlQuery, Map.of("mpaId", mpaId), String.class);
            if (mpaName == null) {
                log.error(errorSql);
                throw new InternalServiceException(thisService, jdbc.getClass().getName(), errorSql);
            } else {
                return new Mpa(mpaId, mpaName);
            }
        } catch (EmptyResultDataAccessException e) {
            var warn = String.format("MPA с ID %d не найден в БД", mpaId);
            log.warn(warn);
            throw new EntityNotFoundException(thisService, jdbc.getClass().getName(), warn);
        }
    }

    /**
     * Метод получает из БД все MPA-рейтинги
     *
     * @return список всех имеющихся рейтингов {@link Mpa}
     */
    @Override
    public List<Mpa> getAllMpa() {
        log.info("Получение всех MPA-рейтингов из БД");
        String sqlQuery = "select * from MPA_RATINGS order by MPA_RATING_ID_PK";
        return jdbc.query(sqlQuery, ((rs, rowNum) ->
                new Mpa(rs.getInt("MPA_RATING_ID_PK"), rs.getString("MPA_RATING_NAME")
                )));
    }

    /**
     * Метод получает из БД список известных режиссеров
     *
     * @return список известных режиссеров
     */
    @Override
    public List<Director> getAllDirectors() {
        log.info("Получение списка директоров из БД");
        String sqlQuery = """
                select *
                from DIRECTORS order by DIRECTOR_ID_PK""";
        return jdbc.query(sqlQuery, ((rs, rowNum) ->
                new Director(rs.getInt("DIRECTOR_ID_PK"), rs.getString("DIRECTOR_NAME"))));
    }
}

package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.*;

/**
 * Репозиторий, реализующий интерфейс {@link FilmRepository} для БД
 */
@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final DataSource source;
    private final String thisService = this.getClass().getName();

    /**
     * Метод создает запись о фильме в БД.
     *
     * @param film запись о фильме, которую нужно создать в БД
     * @return новая запись о фильме с установленным ID из БД, либо пустое значение, если запись не создана
     */
    @Override
    public Optional<Film> createFilm(Film film) {
        log.info("Создание записи о фильме в БД");
        if (film == null) {
            return Optional.empty();
        }
        SimpleJdbcInsert simpleJdbc = new SimpleJdbcInsert(source);
        Map<String, Object> parameters = Map.of(
                "FILM_NAME", film.getName(),
                "FILM_DESCRIPTION", film.getDescription(),
                "FILM_RELEASE_DATE", film.getReleaseDate(),
                "FILM_DURATION", film.getDuration(),
                "FILM_MPA_RATING_FK", film.getMpa().getId());
        var generatedID = simpleJdbc.withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID_PK")
                .executeAndReturnKey(parameters).intValue();
        if (generatedID <= 0) {
            log.error("Ошибка! БД вернула для фильма некорректный ID = {}", generatedID);
            return Optional.empty();
        } else {
            film.setId(generatedID);
            updateFilmsGenresTable(film);
            updateFilmsDirectorsTable(film);
            log.info("Запись о фильме ID = {} успешно создана в БД", generatedID);
            return Optional.of(film);
        }
    }

    /**
     * Метод обновляет существующую запись о фильме в БД.
     *
     * @param film фильм с обновленными полями.
     * @return обновленная запись о фильме, либо пустое значение, если запись не была найдена в БД.
     */
    @Override
    public Optional<Film> updateFilm(Film film) {
        log.info("Обновление записи о фильме в БД");
        int filmId = film.getId();
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
                .addValue("mpaId", film.getMpa().getId());
        var dbUpdatedRows = jdbc.update(sqlQuery, paramSource);
        if (dbUpdatedRows > 1) {
            String error = "Критическая ошибка! БД обновила больше одного фильма";
            log.error(error);
            throw new InternalServiceException(thisService, jdbc.getClass().getName(), error);
        } else if (dbUpdatedRows == 0) {
            log.warn("Запись не найдена в БД");
            return Optional.empty();
        } else {
            updateFilmsGenresTable(film);
            updateFilmsDirectorsTable(film);
            log.info("Запись о фильме ID = {} успешно обновлена в БД", filmId);
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
        log.info("Создание списка всех фильмов из БД");
        String sqlQuery = """
                select *,
                (SELECT MPA_RATING_NAME FROM MPA_RATINGS WHERE MPA_RATING_ID_PK = FILM_MPA_RATING_FK) AS MPA_NAME,
                (select count(FR_FILM_ID_PK) from FILMS_RATINGS where FR_FILM_ID_PK = FILM_ID_PK) as RATE
                from FILMS
                order by FILM_ID_PK""";
        return jdbc.query(sqlQuery, filmMapper());
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
                select *,
                    (select count(FR_USER_ID_PK)
                    from FILMS_RATINGS
                    where FR_FILM_ID_PK = :filmId) as RATE,
                    (SELECT MPA_RATING_NAME FROM MPA_RATINGS WHERE MPA_RATING_ID_PK = FILM_MPA_RATING_FK) AS MPA_NAME
                    from FILMS
                    where FILM_ID_PK = :filmId""";
        try {
            var film = jdbc.queryForObject(sqlQuery, Map.of("filmId", filmId), filmMapper());
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

    /**
     * Метод возвращает список фильмов из БД с указанными ID.
     *
     * @return список фильмов, может быть пустым
     */
    @Override
    public List<Film> getFilmsByIds(List<Integer> filmsIds) {
        log.info("Создание списка фильмов из БД по списку их ID");

        if (filmsIds.isEmpty())
            return new ArrayList<>();

        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < filmsIds.size() - 1; i++) {
            ids.append(filmsIds.get(i)).append(", ");
        }
        ids.append(filmsIds.getLast());

        String sqlQuery = """
                select *,
                (SELECT MPA_RATING_NAME FROM MPA_RATINGS WHERE MPA_RATING_ID_PK = FILM_MPA_RATING_FK) AS MPA_NAME,
                (select count(FR_FILM_ID_PK) from FILMS_RATINGS where FR_FILM_ID_PK = FILM_ID_PK) as RATE
                from FILMS
                where FILM_ID_PK IN(""" + ids + ")" +
                "order by FILM_ID_PK";
        return jdbc.query(sqlQuery, filmMapper());
    }

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @param topSize размер топа
     * @return список ID фильмов топа в порядке убывания количества лайков
     */
    @Override
    public List<Film> getPopularFilm(Integer topSize) {
        return getPopularFilm(topSize, null, null);
    }

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @param topSize размер топа
     * @param genreId идентификатор жанра
     * @param year    год релиза фильма
     * @return список ID фильмов топа в порядке убывания количества лайков
     */
    @Override
    public List<Film> getPopularFilm(Integer topSize, Integer genreId, Integer year) {
        log.info("Получение топа рейтинга фильмов из БД, размер топа: {}", topSize);
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sqlQuery = """
                select *,
                (select count(FR_USER_ID_PK)
                        from FILMS_RATINGS
                        where FR_FILM_ID_PK = FILM_ID_PK) as RATE,
                (SELECT MPA_RATING_NAME
                        FROM MPA_RATINGS
                        WHERE MPA_RATING_ID_PK = FILM_MPA_RATING_FK) AS MPA_NAME
                  FROM films f
                 WHERE nvl(:year, EXTRACT(YEAR FROM f.FILM_RELEASE_DATE)) = EXTRACT(YEAR FROM f.FILM_RELEASE_DATE)
                   AND (EXISTS(SELECT 1 FROM FILMS_GENRES fg
                                WHERE fg.fg_film_id = FILM_ID_PK AND fg.fg_genre_id = :genre_id) OR :genre_id IS null)
                order by RATE desc""";
        if (topSize != null) {
            sqlQuery += " limit :topSize";
            params.addValue("topSize", topSize);
        }
        params.addValue("genre_id", genreId);
        params.addValue("year", year);
        return jdbc.query(sqlQuery, params, filmMapper());
    }

    /**
     * Получение списка фильмов режиссера по условиям
     *
     * @param conditions - дополнение к запросу с условиями поиска
     * @return список найденных фильмов
     */
    @Override
    public List<Film> findFilmsForDirectorByConditions(int directorId, String conditions) {
        String sqlQuery = """
                select *,
                (select count(FR_USER_ID_PK)
                        from FILMS_RATINGS
                        where FR_FILM_ID_PK = FILM_ID_PK) as RATE,
                (SELECT MPA_RATING_NAME
                        FROM MPA_RATINGS
                        WHERE MPA_RATING_ID_PK = FILM_MPA_RATING_FK) AS MPA_NAME
                from FILMS
                join FILMS_DIRECTORS on FILM_ID_PK = FD_FILM_ID
                join DIRECTORS on FD_DIRECTOR_ID = DIRECTOR_ID_PK
                where DIRECTOR_ID_PK = :directorId""";
        sqlQuery = sqlQuery.concat(conditions);
        return jdbc.query(sqlQuery, Map.of("directorId", directorId), filmMapper());
    }

    /**
     * Метод обновляет информацию в репозитории о списке жанров указанного фильма
     *
     * @param film фильм, из которого берется список его жанров
     */
    private void updateFilmsGenresTable(Film film) {
        String sqlQuery = """
                delete from FILMS_GENRES
                where FG_FILM_ID = :filmId""";
        jdbc.update(sqlQuery, new MapSqlParameterSource().addValue("filmId", film.getId()));
        sqlQuery = """
                insert into FILMS_GENRES (FG_FILM_ID, FG_GENRE_ID)
                values (:filmId, :genreId)""";
        int id = film.getId();
        for (var genre : film.getGenres()) {
            jdbc.update(sqlQuery, new MapSqlParameterSource()
                    .addValue("filmId", id)
                    .addValue("genreId", genre.getId()));
        }
    }

    /**
     * Метод обновляет информацию в репозитории о списке режиссеров указанного фильма
     *
     * @param film фильм, из которого берется список его режиссеров
     */
    private void updateFilmsDirectorsTable(Film film) {
        int id = film.getId();
        String sqlQuery = """
                delete from FILMS_DIRECTORS
                where FD_FILM_ID = :filmId""";
        jdbc.update(sqlQuery, new MapSqlParameterSource().addValue("filmId", id));
        sqlQuery = """
                insert into FILMS_DIRECTORS (FD_FILM_ID, FD_DIRECTOR_ID)
                values (:filmId, :directorId)""";
        for (var director : film.getDirectors()) {
            jdbc.update(sqlQuery, new MapSqlParameterSource()
                    .addValue("filmId", id)
                    .addValue("directorId", director.getId()));
        }
    }

    /**
     * Метод получает список жанров фильма из БД.
     *
     * @param filmId ID фильма
     * @return список жанров
     */
    private List<Genre> getFilmGenresFromDb(int filmId) {
        log.info("Получение списка жанров фильма ID {} из БД", filmId);
        String sqlQuery = """
                select FG_GENRE_ID as ID, GENRE_NAME as NAME
                from FILMS_GENRES
                join GENRES on GENRE_ID_PK = FG_GENRE_ID
                where FG_FILM_ID = :filmId
                order by ID""";
        return jdbc.query(sqlQuery, Map.of("filmId", filmId), genreMapper());
    }

    /**
     * Метод получает список режиссеров фильма из БД.
     *
     * @param filmId ID фильма
     * @return список режиссеров
     */
    private Set<Director> getFilmDirectorsFromDb(int filmId) {
        log.info("Получение списка режиссеров фильма ID {} из БД", filmId);
        String sqlQuery = """
                select FD_DIRECTOR_ID as ID, DIRECTOR_NAME as NAME
                from FILMS_DIRECTORS
                join DIRECTORS on DIRECTOR_ID_PK = FD_DIRECTOR_ID
                where FD_FILM_ID = :filmId
                order by ID""";
        return new HashSet<>(jdbc.query(sqlQuery, Map.of("filmId", filmId), directorMapper()));
    }

    /**
     * Метод возвращает список общих с другом фильмов с сортировкой по их популярности
     *
     * @param userId   идентификатор пользователя, запрашивающего информацию
     * @param friendId идентификатор пользователя, с которым необходимо сравнить список фильмов
     * @return возвращает список фильмов, отсортированных по популярности.
     */
    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sqlQuery =
                "SELECT *," +
                        "(SELECT mpa_rating_name FROM mpa_ratings WHERE mpa_rating_id_pk = film_mpa_rating_fk) AS mpa_name," +
                        "(SELECT COUNT(fr_film_id_pk) FROM films_ratings WHERE fr_film_id_pk = film_id_pk) AS rate " +
                        "FROM films " +
                        "WHERE film_id_pk IN(" +
                        "SELECT fr_film_id_pk " +
                        "FROM films_ratings AS fr0 " +
                        "WHERE fr_film_id_pk IN(" +
                        "SELECT DISTINCT fr_film_id_pk " +
                        "FROM films_ratings fr " +
                        "WHERE (fr_film_id_pk IN(SELECT fr_film_id_pk FROM films_ratings fr2 " +
                        "WHERE fr_user_id_pk = :userId)" +
                        "AND fr_film_id_pk IN(SELECT fr_film_id_pk FROM films_ratings fr3 " +
                        "WHERE fr_user_id_pk = :friendId))))" +
                        "ORDER BY rate DESC";

        return jdbc.query(sqlQuery,
                Map.of("userId", userId, "friendId", friendId),
                filmMapper());
    }

    @Override
    public void deleteFilmById(int filmId) {
        log.info("удаление фильма ID {} из БД", filmId);
        String sqlQuery = """
                delete from FILMS
                where FILM_ID_PK = :filmId""";
        jdbc.update(sqlQuery, new MapSqlParameterSource().addValue("filmId", filmId));
    }

    private RowMapper<Genre> genreMapper() {
        return (ResultSet rs, int rowNum) -> new Genre(
                rs.getInt("ID"),
                rs.getString("NAME"));
    }

    private RowMapper<Director> directorMapper() {
        return (ResultSet rs, int rowNum) -> new Director(
                rs.getInt("ID"),
                rs.getString("NAME"));
    }

    private RowMapper<Film> filmMapper() {
        return (ResultSet rs, int rowNum) -> new Film(
                rs.getInt("FILM_ID_PK"),
                rs.getString("FILM_NAME"),
                rs.getString("FILM_DESCRIPTION"),
                rs.getDate("FILM_RELEASE_DATE").toLocalDate(),
                rs.getInt("FILM_DURATION"),
                rs.getInt("RATE"),
                new Mpa(rs.getInt("FILM_MPA_RATING_FK"), rs.getString("MPA_NAME")),
                getFilmGenresFromDb(rs.getInt("FILM_ID_PK")),
                getFilmDirectorsFromDb(rs.getInt(("FILM_ID_PK"))));
    }

    //Ищем в БД все фильмы по режисёру и названию фильма
    @Override
    public List<Film> search(String title, String director) {

        log.info("Параметры на вход title ={} и director = {}", title, director);
        String sqlSelect = "";
        List<Film> filmsLilst = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (title.isEmpty() && !director.isEmpty()) {
            log.info("Сработало правило title.isEmpty()");
            sqlSelect = """
                    SELECT FILM_ID_PK FROM (SELECT f.*
                    FROM FILMS f
                    LEFT JOIN (SELECT FR_FILM_ID_PK, COUNT(*) AS num_likes
                    FROM FILMS_RATINGS
                    GROUP BY FR_FILM_ID_PK) r ON f.FILM_ID_PK = r.FR_FILM_ID_PK
                    LEFT JOIN FILMS_DIRECTORS fd ON f.FILM_ID_PK = fd.FD_FILM_ID
                    LEFT JOIN DIRECTORS d ON fd.FD_DIRECTOR_ID = d.DIRECTOR_ID_PK
                    WHERE LOWER(d.DIRECTOR_NAME) LIKE LOWER(:directorName)
                    ORDER BY r.num_likes DESC)
                    """;
            params.put("directorName", "%" + director + "%");
        }
        if (director.isEmpty() && !title.isEmpty()) {
            log.info("Сработало правило director.isEmpty()");
            sqlSelect = """
                    SELECT FILM_ID_PK FROM (SELECT f.*
                    FROM FILMS f
                    LEFT JOIN (SELECT FR_FILM_ID_PK, COUNT(*) AS num_likes
                    FROM FILMS_RATINGS
                    GROUP BY FR_FILM_ID_PK) r ON f.FILM_ID_PK = r.FR_FILM_ID_PK
                    LEFT JOIN FILMS_DIRECTORS fd ON f.FILM_ID_PK = fd.FD_FILM_ID
                    LEFT JOIN DIRECTORS d ON fd.FD_DIRECTOR_ID = d.DIRECTOR_ID_PK
                    WHERE LOWER(f.FILM_NAME) LIKE LOWER(:filmName)
                    ORDER BY r.num_likes DESC)
                    """;

            params.put("filmName", "%" + title + "%");
        }
        if (!title.isBlank() && !director.isBlank()) {
            log.info("Сработало правило !title.isBlank() && !director.isBlank()");
            sqlSelect = """
                                SELECT FILM_ID_PK FROM (SELECT f.*
                                 FROM FILMS f
                                 LEFT JOIN (SELECT FR_FILM_ID_PK, COUNT(*) AS num_likes
                                 FROM FILMS_RATINGS
                                 GROUP BY FR_FILM_ID_PK) r ON f.FILM_ID_PK = r.FR_FILM_ID_PK
                                 LEFT JOIN FILMS_DIRECTORS fd ON f.FILM_ID_PK = fd.FD_FILM_ID
                                 LEFT JOIN DIRECTORS d ON fd.FD_DIRECTOR_ID = d.DIRECTOR_ID_PK
                                 WHERE FILM_ID_PK IN 
                                (SELECT f.FILM_ID_PK FROM FILMS f WHERE 
                                                   LOWER( FILM_NAME ) LIKE 
                                            LOWER (:filmName) OR  (LOWER(DIRECTOR_NAME) LIKE LOWER(:directorName)))
                                 ORDER BY r.num_likes DESC) AS cmplx
                                \s
                                 """;
            params.put("filmName", "%" + title + "%");
            params.put("directorName", "%" + director + "%");

        }

        jdbc.query(sqlSelect, params, getIntFromDb());
        List<Integer> filmIdLilst = jdbc.query(sqlSelect, params, getIntFromDb());
        for (Integer filmId : filmIdLilst) {
            filmsLilst.add(getFilm(filmId).get());
        }
        return filmsLilst;
    }

    private RowMapper<Integer> getIntFromDb() {
        return (ResultSet rs, int rowNum) -> rs.getInt("FILM_ID_PK");
    }
}
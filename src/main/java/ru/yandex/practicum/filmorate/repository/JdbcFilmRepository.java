package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
import ru.yandex.practicum.filmorate.exception.EntityValidateException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.DEFAULT_MPA_RATING;

@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final DataSource source;
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность Film = null";
    private final String idError = "Ошибка! ID сущности может быть только положительным значением";

    /**
     * Метод создает запись о фильме в БД.
     *
     * @param film запись о фильме, которую нужно создать в БД
     * @return новая запись о фильме с установленным ID из БД, либо пустое значение, если запись не создана
     */
    @Override
    public Optional<Film> createFilm(@NotNull(message = entityNullError) Film film) {
        log.info("Создание записи о фильме в БД");
        validateAndUpdateFilm(film);
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
    public Optional<Film> updateFilm(@NotNull(message = entityNullError) Film film) {
        log.info("Обновление записи о фильме в БД");
        int filmId = film.getId();
        validateAndUpdateFilm(film);
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
    public Optional<Film> getFilm(@Positive(message = idError) int filmId) {
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
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @param topSize размер топа
     * @return список ID фильмов топа в порядке убывания количества лайков
     */
    @Override
    public List<Film> getPopularFilm(@Positive(message = idError) int topSize) {
        log.info("Получение топа рейтинга фильмов из БД, размер топа: {}", topSize);
        String sqlQuery = """
                select *,
                (select count(FR_USER_ID_PK)
                        from FILMS_RATINGS
                        where FR_FILM_ID_PK = FILM_ID_PK) as RATE,
                (SELECT MPA_RATING_NAME
                        FROM MPA_RATINGS
                        WHERE MPA_RATING_ID_PK = FILM_MPA_RATING_FK) AS MPA_NAME
                from FILMS
                order by RATE desc
                limit :topSize""";
        return jdbc.query(sqlQuery, Map.of("topSize", topSize), filmMapper());
    }

    /**
     * Метод возвращает список MPA-рейтингов из БД и максимально допустимый идентификатор MPA-рейтинга
     * для использования в последующих проверках
     *
     * @return {@link MpaProperties}
     */
    private MpaProperties getMpaProperties() {
        log.info("Получение свойств MPA-рейтинга из БД");
        String sqlQuery = """
                select *
                from MPA_RATINGS""";
        var listMpa = jdbc.query(sqlQuery, ((rs, rowNum) ->
                new Mpa(rs.getInt("MPA_RATING_ID_PK"), rs.getString("MPA_RATING_NAME")
                )));
        listMpa.sort(Mpa::compareTo);
        return new MpaProperties(listMpa, listMpa.size());
    }

    /**
     * Метод возвращает список известных жанров из БД и максимально допустимый идентификатор жанра
     * для использования в последующих проверках
     *
     * @return {@link GenresProperties}
     */
    private GenresProperties getGenresProperties() {
        log.info("Получение свойств для списка жанров из БД");
        String sqlQuery = """
                select *
                from GENRES""";
        var listGenres = jdbc.query(sqlQuery, ((rs, rowNum) ->
                new Genre(rs.getInt("GENRE_ID_PK"), rs.getString("GENRE_NAME"))));
        listGenres.sort(Genre::compareTo);
        return new GenresProperties(listGenres, listGenres.size());
    }

    /**
     * Метод возвращает список известных режиссеров из БД и максимально допустимый идентификатор режиссера
     * для использования в последующих проверках
     *
     * @return {@link DirectorProperties}
     */
    private DirectorProperties getDirectorProperties() {
        log.info("Получение свойств для списка директоров из БД");
        String sqlQuery = """
                select *
                from DIRECTORS""";
        var listDirectors = jdbc.query(sqlQuery, ((rs, rowNum) ->
                new Director(rs.getInt("DIRECTOR_ID_PK"), rs.getString("DIRECTOR_NAME"))));
        listDirectors.sort(Director::compareTo);
        return new DirectorProperties(listDirectors, listDirectors.size());
    }

    /**
     * Метод обновляет информацию в репозитории о списке жанров указанного фильма
     *
     * @param film фильм, из которого берется список его жанров
     */
    void updateFilmsGenresTable(@NotNull(message = entityNullError) Film film) {
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
    void updateFilmsDirectorsTable(@NotNull(message = entityNullError) Film film) {
        String sqlQuery = """
                delete from FILMS_DIRECTORS
                where FD_DIRECTOR_ID = :filmId""";
        jdbc.update(sqlQuery, new MapSqlParameterSource().addValue("filmId", film.getId()));
        sqlQuery = """
                insert into FILMS_DIRECTORS (FD_FILM_ID, FD_DIRECTOR_ID)
                values (:filmId, :directorId)""";
        int id = film.getId();
        for (var director : film.getDirector()) {
            jdbc.update(sqlQuery, new MapSqlParameterSource()
                    .addValue("filmId", id)
                    .addValue("directorId", director.getId()));
        }
    }

    /**
     * Метод проверяет, что ID MPA-рейтинга, ID в списках жанров и режиссеров имеются в БД, 
     * и присваивает соответствующие названия полям фильма по этим ID. В списках удаляются повторы.
     *
     * @param film фильм, в котором нужно проверить ID и присвоить полям названия
     */
    private void validateAndUpdateFilm(@NotNull(message = entityNullError) Film film) {
        film.setMpa(getMpa(film));
        var genres = film.getGenres();
        var genreProperties = getGenresProperties();
        var sortedGenres = new TreeSet<>(Genre::compareTo);
        if (genres != null) {
            if (genres.stream().anyMatch(genre -> genre.getId() > genreProperties.maxGenreId)) {
                throw new EntityValidateException(thisService,
                        "Ошибка валидации параметров запроса", "ID жанра превышает число известных в БД");
            }
            genres.forEach(genre -> genre.setName(genreProperties.genres.get(genre.getId()-1).getName()));
            sortedGenres.addAll(genres);
        }
        film.setGenres(sortedGenres.stream().toList());
        var directors = film.getDirector();
        var directorProperties = getDirectorProperties();
        var sortedDirectors = new TreeSet<>(Director::compareTo);
        if (directors != null) {
            if (directors.stream().anyMatch(director -> director.getId() > directorProperties.maxDirectorId)) {
                throw new EntityValidateException(thisService,
                        "Ошибка валидации параметров запроса", "ID директора превышает число известных в БД");
            }
            directors.forEach(director ->
                    director.setName(directorProperties.directors.get(director.getId()-1).getName()));
            sortedDirectors.addAll(directors);
        }
        film.setDirector(sortedDirectors.stream().toList());
    }

    private Mpa getMpa(@NotNull(message = entityNullError) Film film) {
        var mpa = film.getMpa();
        var mpaId = DEFAULT_MPA_RATING;
        var mpaProperties = getMpaProperties();
        if (mpa == null) {
            mpa = new Mpa(mpaId, mpaProperties.getMpa().getFirst().getName());
        } else {
            mpaId = mpa.getId();
            if (mpaId > mpaProperties.maxMpaId) {
                throw new EntityValidateException(thisService,
                        "Ошибка валидации параметров запроса", "ID MPA-рейтинга превышает число известных в БД");
            } else {
                mpa = new Mpa(mpaId, mpaProperties.getMpa().get(mpaId - 1).getName());
            }
        }
        return mpa;
    }

    /**
     * Метод получает список жанров фильма из БД.
     *
     * @param filmId ID фильма
     * @return список жанров
     */
    private List<Genre> getFilmGenresFromDb(@Positive(message = idError) int filmId) {
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
    private List<Director> getFilmDirectorsFromDb(@Positive(message = idError) int filmId) {
        log.info("Получение списка режиссеров фильма ID {} из БД", filmId);
        String sqlQuery = """
                select FD_DIRECTOR_ID as ID, DIRECTOR_NAME as NAME
                from FILMS_DIRECTORS
                join DIRECTORS on DIRECTOR_ID_PK = FD_DIRECTOR_ID
                where FD_FILM_ID = :filmId
                order by ID""";
        return jdbc.query(sqlQuery, Map.of("filmId", filmId), directorMapper());
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

    @AllArgsConstructor
    @Getter
    static class MpaProperties {
        List<Mpa> mpa;
        int maxMpaId;
    }

    @AllArgsConstructor
    @Getter
    static class GenresProperties {
        List<Genre> genres;
        int maxGenreId;
    }

    @AllArgsConstructor
    @Getter
    static class DirectorProperties {
        List<Director> directors;
        int maxDirectorId;
    }
}
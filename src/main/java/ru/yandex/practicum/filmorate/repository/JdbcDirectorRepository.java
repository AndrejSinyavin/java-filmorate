package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.*;

/**
 * Репозиторий, реализующий CRUD-операции для режиссера
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepository implements DirectorRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final DataSource source;
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность режиссер = null";
    private final String idError = "Ошибка! ID сущности может быть только положительным значением";

    /**
     * Возвращает список всех режиссеров из БД.
     *
     * @return список режиссеров, может быть пустым
     */
    @Override
    public Collection<Director> findAll() {
        log.info("Создание списка всех режиссеров из БД");
        String sqlQuery = "SELECT * FROM DIRECTORS";
        var sortedDirectors = new TreeSet<>(Director::compareTo);
        sortedDirectors.addAll(jdbc.query(sqlQuery, directorMapper()));
        log.info("Список найденных режиссеров: {}", sortedDirectors);
        return sortedDirectors;
    }

    /**
     * Возвращает режиссера по его ID
     *
     * @param id режиссера
     * @return режиссер, или пустое значение, если не найден
     */
    @Override
    public Optional<Director> findById(@Positive(message = idError) int id) {
        String sqlQuery = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID_PK = :id";
        try {
            var director = jdbc.queryForObject(sqlQuery, Map.of("id", id), directorMapper());
            if (director == null) {
                String error =
                        "Ошибка! SQL-запрос вернул NULL, маппинг получения данных о пользователе выполнен некорректно!";
                log.error(error);
                throw new InternalServiceException(thisService, jdbc.getClass().getName(), error);
            }
            log.info("Режиссер с ID {} найден в БД: {}", id, director);
            return Optional.of(director);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Режиссер с ID {} не найден в БД", id);
            return Optional.empty();
        }
    }

    /**
     * Создает режиссера в репозитории
     *
     * @param director режиссер, которого нужно создать
     * @return он же с установленным ID, или пустое значение, если не получилось
     */
    @Override
    public Optional<Director> create(Director director) {
        log.info("Создание записи о режиссере в БД: {} ", director);
        if (director == null || director.getName() == null) {
            throw new InternalServiceException(thisService, "Создание режиссера", entityNullError);
        }
        String name = director.getName();
        try {
            SimpleJdbcInsert simpleJdbc = new SimpleJdbcInsert(source);
            var generatedID = simpleJdbc.withTableName("DIRECTORS")
                    .usingGeneratedKeyColumns("DIRECTOR_ID_PK")
                    .executeAndReturnKey(Map.of("DIRECTOR_NAME", name)).intValue();
            if (generatedID == 0) {
                log.warn("Запись не создана!");
                return Optional.empty();
            } else {
                director.setId(generatedID);
                log.info("Запись о режиссере ID = {} успешно создана в БД", generatedID);
                return Optional.of(director);
            }
        } catch (DuplicateKeyException e) {
            String warn = String.format("В БД уже есть режиссер %s", name);
            log.warn(warn);
            throw new EntityAlreadyExistsException(thisService, "Создание режиссера", warn);
        }
    }

    /**
     * Обновление существующего режиссера
     *
     * @param director режиссер с целевым ID
     * @return он же, или пустое значение, если не получилось
     */
    @Override
    public Optional<Director> update(@NotNull(message = entityNullError) Director director) {
        int directorId = director.getId();
        log.info("Обновление записи о режиссере ID {} в БД", directorId);
        String sqlQuery = """
                update DIRECTORS set
                DIRECTOR_NAME = :name
                where DIRECTOR_ID_PK = :id""";
        var dbUpdatedRows = jdbc.update(sqlQuery, Map.of("name", director.getName(), "id", directorId));
        if (dbUpdatedRows > 1) {
            String error = "Критическая ошибка! БД обновила больше одной записи";
            log.error(error);
            throw new InternalServiceException(thisService, jdbc.getClass().getName(), error);
        } else if (dbUpdatedRows == 0) {
            log.warn("Запись не найдена в БД");
            return Optional.empty();
        } else {
            log.info("Запись о режиссере ID = {} успешно обновлена в БД", directorId);
            return Optional.of(director);
        }
    }

    /**
     * Удаление режиссера
     *
     * @param directorId Id режиссера
     */
    @Override
    public void delete(@Positive(message = idError) int directorId) {
        log.info("Удаление режиссера из БД");
        String sqlQuery = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID_PK = :directorId";
        var deletedRow = jdbc.update(sqlQuery, Map.of("directorId", directorId));
        log.info("Успешно удалена {} запись", deletedRow);
    }

    private RowMapper<Director> directorMapper() {
        return (ResultSet rs, int rowNum) -> new Director(
                rs.getInt("DIRECTOR_ID_PK"),
                rs.getString("DIRECTOR_NAME"));
    }
}

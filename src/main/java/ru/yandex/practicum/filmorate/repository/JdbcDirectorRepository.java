package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.entity.Film;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий, реализующий CRUD-операции для режиссера
 */
@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepository implements DirectorRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность Film = null";
    private final String idError = "Ошибка! ID сущности может быть только положительным значением";

    /**
     * Возвращает список всех режиссеров из Бд.
     *
     * @return список режиссеров, может быть пустым
     */
    @Override
    public List<Director> findAll() {
        log.info("Создание списка всех режиссеров из БД");
        String sqlQuery = """
                select *
                from FILMS_DIRECTORS
                order by FILM_ID_PK""";
        return jdbc.query(sqlQuery, directorMapper());
    }

    /**
     * Возвращает режиссера по его ID
     *
     * @param id режиссера
     * @return режиссер, или пустое значение, если не найден
     */
    @Override
    public Optional<Director> findById(@Positive(message = idError) int id) {
        return Optional.empty();
    }

    /**
     * Создает режиссера в репозитории
     *
     * @param director режиссер, которого нужно создать
     * @return он же с установленным ID, или пустое значение, если не получилось
     */
    @Override
    public Optional<Director> create(@NotNull(message = entityNullError) Director director) {

        return Optional.empty();
    }

    /**
     * Обновление существующего режиссера
     *
     * @param director режиссер с целевым ID
     * @return он же, или пустое значение, если не получилось
     */
    @Override
    public Optional<Director> update(@NotNull(message = entityNullError) Director director) {
        return Optional.empty();
    }

    /**
     * Удаление режиссера
     *
     * @param directorId Id режиссера
     */
    @Override
    public void delete(@Positive(message = idError) int directorId) {

    }

    private RowMapper<Director> directorMapper() {
        return (ResultSet rs, int rowNum) -> new Director(
                rs.getInt("ID"),
                rs.getString("NAME"));
    }
}

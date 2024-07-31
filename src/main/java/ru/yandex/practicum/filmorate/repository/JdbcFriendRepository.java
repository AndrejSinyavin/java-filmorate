package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcFriendRepository implements FriendRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final JdbcTemplate jdbcTemplate;
    private final String thisService = this.getClass().getName();
    private final String idError = "Ошибка! ID пользователя может быть только положительным значением";

    /**
     * Метод создает запрос на дружбу.
     *
     * @param firstUserId  ID пользователя, создающего запрос
     * @param secondUserId ID пользователя, к которому добавляются
     */
    @Override
    public void addFriend(@Positive(message = idError) int firstUserId,
                          @Positive(message = idError) int secondUserId) {
        log.info("Создание записи в БД о добавлении пользователя в друзья к другому пользователю");
        SimpleJdbcInsertOperations simpleJdbc = new SimpleJdbcInsert(jdbcTemplate);
        try {
            simpleJdbc.withTableName("FRIENDSHIP_STATUSES")
                    .execute(Map.of("FS_USER_ID", firstUserId, "FS_FRIEND_ID", secondUserId));
        } catch (DuplicateKeyException e) {
            String warn = "Такая запись на добавление в друзья уже существует в БД";
            log.warn(warn, e);
            throw new EntityAlreadyExistsException(thisService, e.getClass().getName(), warn);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            throw new EntityNotFoundException(thisService, simpleJdbc.getClass().getName(),
                    "Пользователи с указанными id не найдены");
        }
    }

    /**
     * Метод удаляет имеющийся запрос/подтверждение дружбы.
     *
     * @param firstUserId  ID пользователя, удаляющего запрос
     * @param secondUserId ID пользователя, которому предлагали дружбу
     */
    @Override
    public void deleteFriend(@Positive(message = idError) int firstUserId,
                             @Positive(message = idError) int secondUserId) {
        log.info("Удаление записи в БД о добавлении пользователя в друзья к другому пользователю");
        var paramSource = new MapSqlParameterSource()
                .addValue("firstUserId", firstUserId)
                .addValue("secondUserId", secondUserId);
        checkId(firstUserId);
        checkId(secondUserId);
        String sqlQuery = """
                delete from FRIENDSHIP_STATUSES
                where FS_USER_ID = :firstUserId AND FS_FRIEND_ID = :secondUserId""";
        if (jdbc.update(sqlQuery, paramSource) == 0) {
            log.warn("Пользователи не добавляли друг друга в друзья");
        }
    }

    /**
     * Метод возвращает список друзей указанного пользователя
     *
     * @param userId ID пользователя
     * @return список ID друзей (может быть пустым, если нет друзей, отправивших встречный запрос/подтверждение)
     */
    @Override
    public List<User> getFriends(@Positive(message = idError) int userId) {
        log.info("Получение из БД списка друзей пользователя по его ID");
        var paramSource = new MapSqlParameterSource().addValue("userId", userId);
        checkId(userId);
        String sqlQuery = """
                select * from USERS
                where USER_ID_PK in (select FS_FRIEND_ID
                                    from FRIENDSHIP_STATUSES
                                    where FS_USER_ID = :userId
                                    order by FS_FRIEND_ID)""";
        paramSource = new MapSqlParameterSource().addValue("userId", userId);
        return jdbc.query(sqlQuery, paramSource, userMapper());
    }

    /**
     * Метод возвращает список общих друзей двух пользователей.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return список ID общих друзей (может быть пустым)
     */
    @Override
    public List<User> getCommonFriends(@Positive(message = idError) int firstUserId,
                                       @Positive(message = idError) int secondUserId) {
        log.info("Получение из БД списка общих друзей двух пользователей");
        String sqlQuery = """
                select * from USERS
                where USER_ID_PK in (select FS_FRIEND_ID
                                    from FRIENDSHIP_STATUSES
                                    where FS_USER_ID = :secondUserId
                                    and FS_FRIEND_ID in (select FS_FRIEND_ID
                                                         from FRIENDSHIP_STATUSES
                                                         where FS_USER_ID = :firstUserId)
                order by USER_ID_PK)""";
        var paramSource = new MapSqlParameterSource()
                .addValue("firstUserId", firstUserId)
                .addValue("secondUserId", secondUserId);
        checkId(firstUserId);
        checkId(secondUserId);
        return jdbc.query(sqlQuery, paramSource, userMapper());
    }

    private RowMapper<User> userMapper() {
        return (ResultSet rs, int rowNum) -> new User(
                rs.getInt("USER_ID_PK"),
                rs.getString("USER_LOGIN"),
                rs.getString("USER_NAME"),
                rs.getString("USER_EMAIL"),
                rs.getDate("USER_BIRTHDAY").toLocalDate());
    }

    private void checkId(@Positive int userId) {
        String sqlQuery = """
                select USER_ID_PK from USERS
                where USER_ID_PK = :userId""";
        var paramSource = new MapSqlParameterSource().addValue("userId", userId);
        try {
            jdbc.queryForObject(sqlQuery, paramSource, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            String warn = String.format("Пользователь с ID %d не найден в БД", userId);
            log.warn(warn);
            throw new EntityNotFoundException(thisService, jdbc.getClass().getName(), warn);
        }
    }
}

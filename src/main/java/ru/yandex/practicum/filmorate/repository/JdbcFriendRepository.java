package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier
@RequiredArgsConstructor
@Valid
public class JdbcFriendRepository implements FriendRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final JdbcTemplate jdbcTemplate;
    private final String thisService = this.getClass().getName();
    private final String idError = "Ошибка! ID пользователя может быть только положительным значением";

    /**
     * Метод добавляет пользователя в друзья к другому пользователю.
     *
     * @param firstUserId  ID пользователя, создающего запрос
     * @param secondUserId ID пользователя, к которому добавляются
     * @return пустое значение если запрос успешно добавлен, иначе - текст ошибки
     */
    @Override
    public Optional<String> addFriend(@Positive(message = idError) int firstUserId,
                                      @Positive(message = idError) int secondUserId) {
        Optional<String> error = validateUserIds(firstUserId, secondUserId);
        if (error.isPresent()) return error;
        log.info("Создание записи в БД о добавлении пользователя в друзья к другому пользователю");
        SimpleJdbcInsertOperations simpleJdbc = new SimpleJdbcInsert(jdbcTemplate);
        try {
            simpleJdbc.withTableName("FRIENDSHIP_STATUSES")
                            .execute(Map.of("FS_USER_ID", firstUserId,"FS_FRIEND_ID", secondUserId));
            return Optional.empty();
        } catch (DuplicateKeyException e) {
            String warn = "Такая запись на добавление в друзья уже существует";
            log.warn(warn, e);
            throw new EntityAlreadyExistsException(thisService, e.getClass().getName(), warn);
        }
    }

    /**
     * Метод удаляет пользователя из списка друзей другого пользователя.
     *
     * @param firstUserId ID пользователя, удаляющего запрос
     * @param secondUserId ID пользователя, которому предлагали дружбу
     * @return пустое значение если удаление успешно, иначе - текст ошибки
     */
    @Override
    public Optional<String> deleteFriend(@Positive(message = idError) int firstUserId,
                                         @Positive(message = idError) int secondUserId) {
        log.info("Удаление записи в БД о добавлении пользователя в друзья к другому пользователю");
        String sqlQuery = """
                          delete from FRIENDSHIP_STATUSES
                          where FS_USER_ID = :firstUserId AND FS_FRIEND_ID = :secondUserId""";
        var paramSource = new MapSqlParameterSource()
                .addValue("firstUserId", firstUserId)
                .addValue("secondUserId", secondUserId);
        if (jdbc.update(sqlQuery, paramSource) != 0) {
            return Optional.empty();
        } else {
            return Optional.of("Пользователь с указанным ID в БД не найден!");
        }
    }

    /**
     * Метод возвращает список друзей указанного пользователя
     *
     * @param userId ID пользователя
     * @return список ID друзей (может быть пустым)
     */
    @Override
    public List<Integer> getFriends(@Positive(message = idError) int userId) {
        log.info("Получение из БД списка друзей пользователя по его ID");
        String sqlQuery = """
                          select FS_FRIEND_ID from FRIENDSHIP_STATUSES
                          where FS_USER_ID = :userId
                          order by FS_FRIEND_ID""";
        var paramSource = new MapSqlParameterSource().addValue("userId", userId);
        return jdbc.queryForList(sqlQuery, paramSource, Integer.class);
    }

    /**
     * Метод возвращает список общих друзей двух пользователей.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return список ID общих друзей (может быть пустым)
     */
    @Override
    public List<Integer> getCommonFriends(@Positive(message = idError) int firstUserId,
                                         @Positive(message = idError) int secondUserId) {
        log.info("Получение из БД списка общих друзей двух пользователей");
        String sqlQuery = """
                select distinct FS_FRIEND_ID
                from FRIENDSHIP_STATUSES
                where (FS_USER_ID = :firstUserId or FS_USER_ID = :secondUserId) and
                (FS_FRIEND_ID != :firstUserId and FS_FRIEND_ID != :secondUserId)
                order by FS_FRIEND_ID""";
        var paramSource = new MapSqlParameterSource()
                .addValue("firstUserId", firstUserId)
                .addValue("secondUserId", secondUserId);
        return jdbc.queryForList(sqlQuery, paramSource, Integer.class);
    }

    private Optional<String> validateUserIds(int firstUserId, int secondUserId) {
        log.info("Проверка, что в БД есть записи о пользователях ID {} и ID {}", firstUserId, secondUserId);
        String sqlQuery = "select count(USER_ID_PK) " +
                          "from USERS " +
                          "where USER_ID_PK = :userOne or USER_ID_PK = :userTwo";
        var paramSource = new MapSqlParameterSource()
                .addValue("userOne", firstUserId)
                .addValue("userTwo", secondUserId);
        Integer result = jdbc.queryForObject(sqlQuery, paramSource, Integer.class);
        if (result == null) {
            String error = "SQL-запрос вернул NULL, ошибка в структуре запроса!";
            log.error(error);
            throw new InternalServiceException(thisService, this.getClass().getName(), error);
        } else if (result != 2) {
            return Optional.of("Один или оба пользователя отсутствуют в БД!");
        } else return Optional.empty();
    }
}

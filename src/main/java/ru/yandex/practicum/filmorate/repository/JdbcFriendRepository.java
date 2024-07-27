package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcFriendRepository implements FriendRepository {
    private final UtilRepository utils;
    private final NamedParameterJdbcOperations jdbc;
    private final JdbcTemplate jdbcTemplate;
    private final String thisService = this.getClass().getName();
    private final String idError = "Ошибка! ID пользователя может быть только положительным значением";

    /**
     * Метод создает запрос на дружбу, или подтверждает уже имеющийся запрос.
     *
     * @param firstUserId  ID пользователя, создающего запрос
     * @param secondUserId ID пользователя, к которому добавляются
     * @return пустое значение если запрос успешно добавлен,
     * иначе - текст ошибки, если пользователи не найдены или уже есть запрос/подтверждение на добавление
     */
    @Override
    public Optional<String> addFriend(@Positive(message = idError) int firstUserId,
                                      @Positive(message = idError) int secondUserId) {
//        utils.validateUserIds(firstUserId, secondUserId);
        log.info("Создание записи в БД о добавлении пользователя в друзья к другому пользователю");
        SimpleJdbcInsertOperations simpleJdbc = new SimpleJdbcInsert(jdbcTemplate);
        try {
            simpleJdbc.withTableName("FRIENDSHIP_STATUSES")
                            .execute(Map.of("FS_USER_ID", firstUserId,"FS_FRIEND_ID", secondUserId));
            return Optional.empty();
        } catch (DuplicateKeyException e) {
            String warn = "Такая запись на добавление в друзья уже существует в БД";
            log.warn(warn, e);
            throw new EntityAlreadyExistsException(thisService, e.getClass().getName(), warn);
        }
    }

    /**
     * Метод удаляет имеющийся запрос/подтверждение дружбы.
     *
     * @param firstUserId ID пользователя, удаляющего запрос
     * @param secondUserId ID пользователя, которому предлагали дружбу
     * @return пустое значение если удаление успешно, иначе - текст ошибки, если указанный запрос не найден
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
            return Optional.of("Указанная запись в БД не найдена");
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
        String sqlQuery = """
                select * from USERS
                where USER_ID_PK in (select FS_FRIEND_ID
                                    from FRIENDSHIP_STATUSES
                                    where FS_USER_ID = :userId
                                    order by FS_FRIEND_ID)""";
        var paramSource = new MapSqlParameterSource().addValue("userId", userId);
        return jdbc.query(sqlQuery, paramSource, new BeanPropertyRowMapper<>(User.class));
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
                where USER_ID_PK in (
                                    select distinct FS_USER_ID
                                    from FRIENDSHIP_STATUSES
                                    where (FS_FRIEND_ID = :firstUserId or FS_FRIEND_ID = :secondUserId)
                                    and (FS_USER_ID != :firstUserId and FS_USER_ID != :secondUserId))""";
        var paramSource = new MapSqlParameterSource()
                .addValue("firstUserId", firstUserId)
                .addValue("secondUserId", secondUserId);
        return jdbc.query(sqlQuery, paramSource, new BeanPropertyRowMapper<>(User.class));
    }
}

package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий реализует логику работы с БД через интерфейс {@link UserRepository}
 */
@Slf4j
@Repository
@Qualifier
@RequiredArgsConstructor
@Valid
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final DataSource source;
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность User = null";
    private final String idError = "Ошибка! ID пользователя может быть только положительным значением";
    private final String dbError = "Сбой в работе СУБД";
    private final String userExist = "Пользователь с указанным логином и/или email уже имеется в базе данных";

    /**
     * Метод создает в таблице пользователей БД нового пользователя с уникальными ID, login и email.
     *
     * @param user запись о пользователе, которую нужно создать
     * @return эта же запись с уникальным новым ID из БД
     */
    @Override
    public Optional<User> createUser(@NotNull(message = entityNullError) User user) {
        log.info("Создание записи о пользователе в БД:");
        SimpleJdbcInsert simpleJdbc = new SimpleJdbcInsert(source);
        Map<String, Object> parameters = Map.of(
                "USER_LOGIN", user.getLogin(),
                "USER_NAME", user.getName(),
                "USER_EMAIL", user.getEmail(),
                "USER_BIRTHDAY", user.getBirthday());
        simpleJdbc.withTableName("USERS").usingGeneratedKeyColumns("USER_ID_PK");
        try {
            user.setId((Integer) simpleJdbc.executeAndReturnKey(parameters));
            return Optional.of(user);
        } catch (DuplicateKeyException e) {
            log.warn(userExist, e);
            throw new EntityAlreadyExistsException(thisService, e.getClass().getName(), userExist);
        }
    }

    /**
     * Метод обновляет в таблице пользователей БД существующего пользователя.
     *
     * @param user запись о пользователе с нужными обновленными полями. Поиск записи в БД производится по его ID
     * @return эта же запись, если обновление полей записи в БД выполнено успешно; иначе пустое значение
     */
    @Override
    public Optional<User> updateUser(@NotNull(message = entityNullError)User user) {
        log.info("Обновление записи о пользователе в БД:");
        int id = user.getId();
        var parameters = new MapSqlParameterSource(Map.of(
                "USER_ID_PK", id,
                "USER_NAME", user.getName(),
                "USER_LOGIN", user.getLogin(),
                "USER_EMAIL", user.getEmail(),
                "USER_BIRTHDAY", user.getBirthday()
        ));
        try {
            var rowsUpdated = jdbc.update("""
                    update USERS
                    set USER_LOGIN = :USER_LOGIN, USER_NAME = :USER_NAME, USER_EMAIL = :USER_EMAIL,
                    USER_BIRTHDAY = :USER_BIRTHDAY
                    where USER_ID_PK = :USER_ID_PK""",
                    parameters, new GeneratedKeyHolder(), new String[]{"USER_ID_PK"});
            if (rowsUpdated == 0) {
                log.warn("Обновить запись невозможно, пользователь ID {} в БД не найден!", id);
                return Optional.empty();
            } else {
                return Optional.of(user);
            }
        } catch (DuplicateKeyException e) {
            log.warn(userExist, e);
            throw new EntityAlreadyExistsException(thisService, e.getClass().getName(), userExist);
        }
    }

    /**
     * Метод возвращает список всех пользователей из БД.
     *
     * @return список пользователей, может быть пустым
     */
    @Override
    public List<User> getAllUsers() {
        log.info("Получение всех записей о пользователях из БД:");
        String sqlQuery = "select * from USERS order by USER_ID_PK";
        return jdbc.query(sqlQuery, (rs, rowNum) ->
                new User(
                        rs.getInt("USER_ID_PK"),
                        rs.getString("USER_NAME"),
                        rs.getString("USER_LOGIN"),
                        rs.getString("USER_EMAIL"),
                        rs.getDate("USER_BIRTHDAY").toLocalDate()
                ));
    }

    /**
     * Метод возвращает из БД запись о пользователе по его ID
     *
     * @param userId ID искомого пользователя
     * @return запись о пользователе; либо пустое значение, если не найден
     */
    @Override
    public Optional<User> getUser(@Positive(message = idError) int userId) {
        log.info("Чтение из БД записи о пользователе");
        String sqlQuery = "select * from USERS where USER_ID_PK = :userId";
        var paramSource = new MapSqlParameterSource().addValue("userId", userId);
        var user = jdbc.queryForObject(sqlQuery, paramSource, (rs, rowNum) ->
                new User(
                        rs.getInt("USER_ID_PK"),
                        rs.getString("USER_NAME"),
                        rs.getString("USER_LOGIN"),
                        rs.getString("USER_EMAIL"),
                        rs.getDate("USER_BIRTHDAY").toLocalDate()
                ));
        return Optional.ofNullable(user);
    }
}
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
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import javax.sql.DataSource;
import java.sql.ResultSet;
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
    private static final String ENTITY_NULL_ERROR = "Ошибка! сущность User = null";
    private static final String ID_ERROR = "Ошибка! ID пользователя может быть только положительным значением";

    /**
     * Метод создает в таблице пользователей БД нового пользователя с уникальными ID, login и email.
     *
     * @param user запись о пользователе, которую нужно создать
     * @return эта же запись с уникальным новым ID из БД
     */
    @Override
    public Optional<User> createUser(@NotNull(message = ENTITY_NULL_ERROR) User user) {
        log.info("Создание записи о пользователе в БД:");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(source)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID_PK");
        Map<String, Object> parameters = Map.of("USER_LOGIN", user.getLogin(),"USER_NAME", user.getName(),
                "USER_EMAIL", user.getEmail(), "USER_BIRTHDAY", user.getBirthday());
        try {
        user.setId((Integer) simpleJdbcInsert.executeAndReturnKey(parameters));
        return Optional.of(user);
        } catch (DuplicateKeyException e) {
            throw new EntityAlreadyExistsException(thisService, simpleJdbcInsert.getClass().getName(),
                    "Пользователь с указанным логином и/или email уже имеется в базе данных");
        }
    }

    /**
     * Метод обновляет в таблице пользователей БД существующего пользователя.
     *
     * @param user запись о пользователе с нужными обновленными полями. Поиск записи в БД производится по его ID
     * @return эта же запись, если обновление полей записи в БД выполнено успешно; иначе пустое значение
     */
    @Override
    public Optional<User> updateUser(@NotNull(message = ENTITY_NULL_ERROR)User user) {
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
            var rowsUpdated = jdbc.update("update USERS " +
                    "set USER_LOGIN = :USER_LOGIN, USER_NAME = :USER_NAME, USER_EMAIL = :USER_EMAIL, " +
                    "USER_BIRTHDAY = :USER_BIRTHDAY " +
                    "where USER_ID_PK = :USER_ID_PK",
                    parameters, new GeneratedKeyHolder(), new String[]{"USER_ID_PK"});
            if (rowsUpdated == 0) {
                log.error("Обновить запись невозможно, пользователь ID {} в БД не найден!", id);
                return Optional.empty();
            } else {
                return Optional.of(user);
            }
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DuplicateKeyException e) {
            throw new EntityAlreadyExistsException(thisService, jdbc.getClass().getName(),
                    "Пользователь с таким логином и/или email уже зарегистрирован в базе данных");
        } catch (DataAccessException e) {
            throw new InternalServiceException(thisService, jdbc.getClass().getName(),
                    "Сбой в работе СУБД");
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
        try {
            return jdbc.query(sqlQuery, (rs, rowNum) ->
                    new User(
                            rs.getInt("USER_ID_PK"),
                            rs.getString("USER_NAME"),
                            rs.getString("USER_LOGIN"),
                            rs.getString("USER_EMAIL"),
                            rs.getDate("USER_BIRTHDAY").toLocalDate()
                    ));
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new InternalServiceException(thisService, jdbc.getClass().getName(),
                    "Сбой в работе СУБД");
        }
    }

    /**
     * Метод возвращает из БД запись о пользователе по его ID
     *
     * @param userId ID искомого пользователя
     * @return запись о пользователе; либо пустое значение, если не найден
     */
    @Override
    public Optional<User> getUser(@Positive(message = ID_ERROR) int userId) {
        log.info("Получение из БД записи о пользователе по его ID:");
        String sqlQuery = "select * from USERS where USER_ID_PK = :USER_ID_PK";
        var paramSource = new MapSqlParameterSource().addValue("USER_ID_PK", userId);
        try {
            var result = jdbc.queryForObject(sqlQuery, paramSource, (rs, rowNum) ->
                    new User(
                            rs.getInt("USER_ID_PK"),
                            rs.getString("USER_NAME"),
                            rs.getString("USER_LOGIN"),
                            rs.getString("USER_EMAIL"),
                            rs.getDate("USER_BIRTHDAY").toLocalDate()
                    ));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new InternalServiceException(thisService, jdbc.getClass().getName(),
                    "Сбой в работе СУБД");
        }
    }
}
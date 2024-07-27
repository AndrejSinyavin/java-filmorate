package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий реализует логику работы с БД через интерфейс {@link UserRepository}
 */
@Slf4j
@Valid
@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final DataSource source;
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность User = null";
    private final String idError = "Ошибка! ID пользователя может быть только положительным значением";
    private final String userExist = "Пользователь с указанным логином и/или email уже имеется в базе данных";

    /**
     * Метод создает в таблице пользователей БД нового пользователя с уникальными ID, login и email.
     *
     * @param user пользователь, которого нужно записать в БД
     * @return эта же запись с уникальным новым ID из БД
     */
    @Override
    public Optional<User> createUser(@NotNull(message = entityNullError) User user) {
        log.info("Создание записи о пользователе в БД");
        SimpleJdbcInsert simpleJdbc = new SimpleJdbcInsert(source);
        Map<String, Object> parameters = Map.of(
                "USER_LOGIN", user.getLogin(),
                "USER_NAME", user.getName(),
                "USER_EMAIL", user.getEmail(),
                "USER_BIRTHDAY", user.getBirthday());
        simpleJdbc.withTableName("USERS").usingGeneratedKeyColumns("USER_ID_PK");
        try {
            int generatedID = simpleJdbc.executeAndReturnKey(parameters).intValue();
            if (generatedID <= 0) {
                String error = "Ошибка! БД вернула для фильма некорректный ID " + generatedID;
                log.error(error);
                throw new InternalServiceException(thisService, jdbc.getClass().getName(), error);
            } else {
                user.setId(generatedID);
                return Optional.of(user);
            }
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
    public Optional<User> updateUser(@NotNull(message = entityNullError) User user) {
        int id = user.getId();
        log.info("Обновление записи о пользователе ID {} в БД", id);
        var parameters = new MapSqlParameterSource(Map.of(
                "userId", id,
                "name", user.getName(),
                "login", user.getLogin(),
                "email", user.getEmail(),
                "birthday", user.getBirthday()
        ));
        try {
            var rowsUpdated = jdbc.update("""
                            update USERS
                            set USER_LOGIN = :login, USER_NAME = :name, USER_EMAIL = :email, USER_BIRTHDAY = :birthday
                            where USER_ID_PK = :userId""",
                    parameters, new GeneratedKeyHolder(), new String[]{"USER_ID_PK"});
            if (rowsUpdated == 0) {
                String warn = "Обновить запись невозможно, пользователь ID " + id + "в БД не найден";
                log.warn(warn);
                throw new EntityNotFoundException(thisService, jdbc.getClass().getName(), warn);
            } else if (rowsUpdated == 1) {
                return Optional.of(user);
            } else {
                return Optional.empty();
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
        log.info("Получение всех записей о пользователях из БД");
        String sqlQuery = "select * from USERS order by USER_ID_PK";
        return jdbc.query(sqlQuery, user_mapper());
    }

    /**
     * Метод возвращает из БД запись о пользователе по его ID
     *
     * @param userId ID искомого пользователя
     * @return запись о пользователе; либо пустое значение, если не найден
     */
    @Override
    public Optional<User> getUser(@Positive(message = idError) int userId) {
        log.info("Чтение из БД записи о пользователе ID {}", userId);
        String sqlQuery = "select * from USERS where USER_ID_PK = :userId";
        var paramSource = new MapSqlParameterSource().addValue("userId", userId);
        try {
            var user = jdbc.queryForObject(sqlQuery, paramSource, user_mapper());
            if (user == null) {
                String error = "Ошибка! SQL-запрос вернул NULL, маппинг из БД в User произведен некорректно";
                log.error(error);
                throw new InternalServiceException(thisService, this.getClass().getName(), error);
            }
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Пользователь ID {} не найден в БД", userId);
            return Optional.empty();
        }
    }

    private RowMapper<User> user_mapper() {
        return (ResultSet rs, int rowNum) -> new User(
                rs.getInt("USER_ID_PK"),
                rs.getString("USER_LOGIN"),
                rs.getString("USER_NAME"),
                rs.getString("USER_EMAIL"),
                rs.getDate("USER_BIRTHDAY").toLocalDate());
    }
}
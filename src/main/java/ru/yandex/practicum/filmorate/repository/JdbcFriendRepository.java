package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
@Valid
public class JdbcFriendRepository implements FriendRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String thisService = this.getClass().getName();
    private static final String ID_ERROR = "Ошибка! ID пользователя может быть только положительным значением";

    /**
     * Метод добавляет запрос на добавление двух пользователей в друзья друг другу.
     *
     * @param firstUserId  ID пользователя, создающего запрос
     * @param secondUserId ID пользователя, которому предлагают дружбу
     * @return пустое значение если запрос выполнен успешно, иначе - текст ошибки
     */
    @Override
    public Optional<String> addFriend(@Positive(message = ID_ERROR) int firstUserId,
                                      @Positive(message = ID_ERROR) int secondUserId) {
        Optional<String> result = validateUserID(firstUserId, secondUserId);
        if (result.isPresent()) return result;
        log.info("Создание запроса на добавление в друзья в БД");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FRIENDSHIP_STATUSES");
        try {
            simpleJdbcInsert.execute(Map.of("FS_USER_ID", firstUserId,"FS_FRIEND_ID", secondUserId));
        } catch (DuplicateKeyException e) {
            throw new EntityAlreadyExistsException(thisService, simpleJdbcInsert.getClass().getName(),
                    "Такой запрос на добавление в друзья уже существует");
        }
        log.info("Ok.");
        return Optional.empty();
    }

    /**
     * Метод удаляет запрос на добавление в друзья.
     *
     * @param firstUserId ID пользователя, удаляющего запрос
     * @param secondUserId ID пользователя, которому предлагали дружбу
     * @return пустое значение если удаление успешно, иначе - текст ошибки
     */
    @Override
    public Optional<String> deleteFriend(@Positive(message = ID_ERROR) int firstUserId,
                                         @Positive(message = ID_ERROR) int secondUserId) {
        Optional<String> result = validateUserID(firstUserId, secondUserId);
        if (result.isPresent()) return result;
        log.info("Удаление запроса на добавление в друзья из БД");
        String sqlQuery = "delete from FRIENDSHIP_STATUSES where FS_USER_ID = ? AND FS_FRIEND_ID = ?";
        if (jdbcTemplate.update(sqlQuery, firstUserId, secondUserId) == 0) {
            return Optional.of("Запрос на добавление в друзья в БД для ID " + firstUserId +
                    " и ID " + secondUserId + "не найден");
        } else {
            return Optional.empty();
        }
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param userId ID пользователя
     * @return список ID друзей (может быть пустым)
     */
    @Override
    public List<Integer> getFriends(@Positive(message = ID_ERROR) int userId) {
        log.info("Получение из БД списка подтвержденных друзей пользователя по его ID");
        String sqlQuery = "select FS_FRIEND_ID from FRIENDSHIP_STATUSES " +
                "where FS_USER_ID = ? and FS_FRIEND_ID in " +
                "(select FS_USER_ID from FRIENDSHIP_STATUSES where FS_FRIEND_ID = ?) " +
                "order by FS_FRIEND_ID";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId, userId);
    }

    /**
     * Метод возвращает список общих друзей двух пользователей.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return список ID общих друзей (может быть пустым)
     */
    @Override
    public List<Integer> getCommonFriends(@Positive(message = ID_ERROR) int firstUserId,
                                         @Positive(message = ID_ERROR) int secondUserId) {
        log.info("Получение из БД списка подтвержденных друзей двух пользователей");
        var firstUserFrendList = getFriends(firstUserId);
        var secondUserFrendList = getFriends(secondUserId);
        var result = new ArrayList<>(firstUserFrendList);
        result.retainAll(secondUserFrendList);
        return result;
    }

    private Optional<String> validateUserID(int firstUserId, int secondUserId) {
        log.info("Проверка, что в БД есть записи о пользователях ID {} и ID {}", firstUserId, secondUserId);
        String sqlQuery = "select count(USER_ID_PK) from USERS where USER_ID_PK = ? or USER_ID_PK = ?";
        try {
            int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, firstUserId, secondUserId);
            if (result != 2) {
                return Optional.of("Один или оба пользователя не зарегистрированы на сервисе!");
            } else return Optional.empty();
        } catch (DataAccessException | NullPointerException e) {
            throw new InternalServiceException(thisService, jdbcTemplate.getClass().getName(),
                    "Не удалось проверить наличие в БД пользователей с указанными ID");
        }
    }
}

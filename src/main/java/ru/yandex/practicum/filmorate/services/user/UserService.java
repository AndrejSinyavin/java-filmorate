package ru.yandex.practicum.filmorate.services.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserServiceInternalException;
import ru.yandex.practicum.filmorate.interfaces.FriendsService;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {
    private static final String ERROR =
            "Сервис работы с пользователями не выполнил задачу из-за отказа в сервисе FriendsService";
    /**
     * Подключение сервиса работы с пользователями.
     */
    private final UserStorage users;
    /**
     * Подключение сервиса работы с друзьями.
     */
    private final FriendsService friends;

    /**
     * Метод добавляет двух пользователей друг другу в друзья.
     *
     * @param userId   ID пользователя
     * @param friendId ID добавляемого в друзья пользователя
     */
    public void addFriend(int userId, int friendId) {
        log.info("Добавление пользователя в друзья:");
        if (!friends.addFriend(userId, friendId)) {
            String message = String.format("Пользователи ID %d и/или ID %d не найдены!", userId, friendId);
            log.error("{}. {}", ERROR, message);
            throw new UserNotFoundException(ERROR, message);
        }
    }

    /**
     * Метод удаляет пользователей из друзей.
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     */
    public void deleteFriend(int userId, int friendId) {
        log.info("Удаление пользователей из друзей:");
        if (!friends.deleteFriend(userId, friendId)) {
            String message = String.format("Пользователи ID %d и/или ID %d не найдены!", userId, friendId);
            log.error("{}. {}", ERROR, message);
            throw new UserNotFoundException(ERROR, message);
        }
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param userId  ID нужного пользователя
     * @return список ID друзей
     */
    public List<User> getFriends(int userId) {
        log.info("Получение списка друзей пользователя:");
        users.getUser(userId);
        try {
            return friends.getFriends(userId).stream()
                    .map(users::getUser)
                    .collect(Collectors.toList());
        } catch (UserNotFoundException e) {
            log.error(ERROR);
            throw new UserNotFoundException(ERROR, e.getMessage());
        }
    }

    /**
     * Метод возвращает список совместных друзей пользователя и его друга
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     * @return  список ID общих друзей
     */
    public List<User> getCommonFriends(int userId, int friendId) {
        log.info("Получение списка общих друзей двух пользователей:");
        var frendsIdSet = friends.getCommonFriends(userId, friendId);
        try {
            return frendsIdSet.stream()
                    .map(users::getUser)
                    .collect(Collectors.toList());
        } catch (UserNotFoundException e) {
            log.error(ERROR);
            throw new UserNotFoundException(ERROR, e.getMessage());
        } catch (NullPointerException e) {
            log.error(ERROR);
            throw new UserNotFoundException(
                    ERROR, String.format("Пользователь ID %d и/или ID %d не найдены!", userId, friendId));
        }
    }

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    public User createUser(User user) {
        log.info("Создание записи о пользователе {} :", user);
        users.createUser(user);
        log.info("Регистрация пользователя в сервисе FriendsService:");
        if (friends.registerNewUser(user.getId())) {
            log.info("Пользователь создан и зарегистрирован на сервисе.");
            return user;
        } else {
            throw new UserServiceInternalException("Сервис работы с пользователями",
                    "Отказ регистрации пользователя в сервисе FriendsService");
        }
    }

    /**
     * Метод обновляет в списке пользователей фильмотеки существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить, поиск производится по ID
     * @return обновленный пользователь
     */
    public User updateUser(User user) {
        log.info("Обновление записи о пользователе {} :", user);
        return users.updateUser(user);
    }

    /**
     * Метод удаляет пользователя из фильмотеки.
     *
     * @param userId удаляемый пользователь
     */
    public void deleteUser(int userId) {
        log.info("Удаление записи о пользователе ID {} :", userId);
        users.deleteUser(userId);
        friends.getFriends(userId)
                .forEach(friendId -> friends.deleteFriend(friendId, userId));
        friends.unregisterUser(userId);
    }

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список всех пользователей, может быть пустым
     */
    public List<User> getAllUsers() {
        log.info("Получение списка всех записей о пользователях:");
        return users.getAllUsers();
    }

    /**
     * Метод возвращает пользователя по его ID
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    public User getUser(int userId) {
        log.info("Получение записи о пользователе ID {} :", userId);
        return users.getUser(userId);
    }

}

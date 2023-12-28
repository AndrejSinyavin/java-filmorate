package ru.yandex.practicum.filmorate.services.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InternalServiceException;
import ru.yandex.practicum.filmorate.interfaces.FriendsService;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {
    private final String thisService = this.getClass().getName();
    /**
     * Подключение сервиса работы с пользователями.
     */
    private final UserStorage users;
    private final String userService = users.getClass().getName();
    /**
     * Подключение сервиса работы с друзьями.
     */
    private final FriendsService friends;
    private final String friendService = friends.getClass().getName();

    /**
     * Метод добавляет двух пользователей друг другу в друзья.
     *
     * @param userId ID пользователя
     * @param friendId ID добавляемого в друзья пользователя
     */
    public void addFriend(int userId, int friendId) {
        log.info("Добавление пользователя в друзья:");
        if (!friends.addFriend(userId, friendId)) {
            throw new EntityNotFoundException(thisService, friendService, String.format(
                    "Пользователи ID %d и/или ID %d не найдены!", userId, friendId));
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
            throw new EntityNotFoundException(thisService,friendService,
                    String.format("Пользователи ID %d и/или ID %d не найдены!", userId, friendId));
        }
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param id ID нужного пользователя
     * @return список ID друзей
     */
    public List<User> getFriends(int id) {
        log.info("Получение списка друзей пользователя:");
        users.getUser(id);
        return friends.getFriends(id).stream()
                .map(users::getUser)
                .map(user -> user.orElseThrow(() ->
                        new EntityNotFoundException(
                                thisService, friendService, String.format("Пользователь ID %d не найден!", id))))
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает список совместных друзей пользователя и его друга
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     * @return список общих друзей
     */
    public List<User> getCommonFriends(int userId, int friendId) {
        log.info("Получение списка общих друзей двух пользователей:");
        var frendsIdSet = friends.getCommonFriends(userId, friendId);
        return frendsIdSet.stream()
                .map(users::getUser)
                .map(user -> user.orElseThrow(() ->
                        new EntityNotFoundException(thisService, userService,
                        String.format("Пользователь ID %d и/или ID %d не найдены!", userId, friendId))))
                .collect(Collectors.toList());
    }

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    public User createUser(User user) {
        log.info("Создание записи о пользователе и его регистрация на сервисе: {} :", user);
        var result = users.createUser(user).orElseThrow(() -> new InternalServiceException(thisService, userService,
                "Ошибка сервиса, не удалось создать запись о пользователе"));
        if (friends.registerUser(user.getId())) {
            log.info("Пользователь создан и зарегистрирован на сервисе.");
        return result;
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
     *
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    public User getUser(int userId) {
        log.info("Получение записи о пользователе ID {} :", userId);
        return users.getUser(userId).orElseThrow();
    }

}

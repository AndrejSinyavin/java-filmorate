package ru.yandex.practicum.filmorate.services.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InternalServiceException;
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
    private final String thisService = this.getClass().getName();
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
     * @param userId ID пользователя
     * @param friendId ID добавляемого в друзья пользователя
     */
    public void addFriend(int userId, int friendId) {
        log.info("Добавление пользователя в друзья:");
        friends.addFriend(userId, friendId).ifPresent(error -> {
            throw new EntityNotFoundException(thisService, friends.getClass().getName(), error); });
    }

    /**
     * Метод удаляет пользователей из друзей.
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     */
    public void deleteFriend(int userId, int friendId) {
        log.info("Удаление пользователей из друзей:");
        friends.deleteFriend(userId, friendId).ifPresent(error -> {
                    throw new EntityNotFoundException(thisService,friends.getClass().getName(), error); });
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param id ID нужного пользователя
     * @return список ID друзей
     */
    public List<User> getFriends(int id) {
        log.info("Получение списка друзей пользователя:");
        String error = String.format(
                "Ошибка при получении списка друзей пользователя: друг ID %d не найден на сервисе!", id);
        users.getUser(id).orElseThrow(() -> new EntityNotFoundException(thisService, users.getClass().getName(),
                String.format("Пользователь ID %d не найден на сервисе!", id)));
        return friends.getFriends(id).orElseThrow(() -> new EntityNotFoundException(
                thisService, friends.getClass().getName(),
                        "Ошибка при получении списка ID друзей пользователя"))
                .stream()
                .map(users::getUser)
                .map(user -> user.orElseThrow(() -> new EntityNotFoundException(
                        thisService, friends.getClass().getName(), error)))
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
        var frendsIdSet = friends.getCommonFriends(userId, friendId).orElseThrow(() ->
                new EntityNotFoundException(thisService, friends.getClass().getName(),
                        String.format("Пользователь ID %d и/или ID %d не найдены!", userId, friendId)));
        return frendsIdSet.stream()
                .map(users::getUser)
                .map(user -> user.orElseThrow(() ->
                        new EntityNotFoundException(thisService, users.getClass().getName(),
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
        var result = users.createUser(user).orElseThrow(() -> new EntityAlreadyExistsException(
                thisService, users.getClass().getName(),
                "Пользователь уже был создан и зарегистрирован на сервисе."));
        friends.registerUser(user.getId()).ifPresent(message -> {
            throw new EntityAlreadyExistsException(thisService, friends.getClass().getName(), message); });
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
        return users.updateUser(user).orElseThrow(() -> new EntityNotFoundException(
                thisService, users.getClass().getName(), "Запись о пользователе на сервисе не найдена"));
    }

    /**
     * Метод удаляет пользователя из фильмотеки.
     *
     * @param userId удаляемый пользователь
     */
    public void deleteUser(int userId) {
        log.info("Удаление записи о пользователе ID {} :", userId);
        users.deleteUser(userId).orElseThrow(() -> new EntityNotFoundException(thisService, users.getClass().getName(),
                String.format("Удалить запись не удалось, пользователь с ID %d не найден!", userId)));
        friends.getFriends(userId).orElseThrow(() -> new InternalServiceException(
                thisService, friends.getClass().getName(),
                        "Ошибка сервиса, не удалось получить список друзей пользователя"))
                .forEach(friendId -> friends.deleteFriend(friendId, userId).ifPresent(message -> {
                    throw new InternalServiceException(thisService, friends.getClass().getName(), message);
                }));
        friends.unregisterUser(userId).ifPresent(message -> {
            throw new InternalServiceException(thisService, friends.getClass().getName(), message);
        });
    }

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список всех пользователей, может быть пустым
     */
    public List<User> getAllUsers() {
        log.info("Получение списка всех записей о пользователях:");
        return users.getAllUsers().orElseThrow(() -> new InternalServiceException(
                thisService, users.getClass().getName(),
                "Ошибка сервиса, не удалось получить список всех пользователей"));
    }

    /**
     * Метод возвращает пользователя по его ID
     *
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    public User getUser(int userId) {
        log.info("Получение записи о пользователе ID {} :", userId);
        return users.getUser(userId).orElseThrow(() -> new EntityNotFoundException(
                thisService, users.getClass().getName(),
                String.format("Получить запись о пользователе не удалось, пользователь с ID %d не найден!", userId)));
    }

}

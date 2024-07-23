package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис содержит логику работы с пользователями, используется контроллером UserController.
 */
@Log4j2
@Service
@AllArgsConstructor
public class UserService implements BaseUserService {
    private final String thisService = this.getClass().getName();
    /**
     * Подключение сервиса работы с пользователями.
     */
    private final UserRepository users;
    /**
     * Подключение сервиса работы с друзьями.
     */
    private final FriendRepository friends;
    /**
     * Подключение сервиса работы с лайками.
     */
    private final LikeRepository likes;

    /**
     * Метод создает запрос на дружбу, или подтверждает уже имеющийся запрос.
     *
     * @param userId ID пользователя
     * @param friendId ID добавляемого в друзья пользователя
     */
    @Override
    public void addFriend(int userId, int friendId) {
        log.info("Запрос/подтверждение дружбы пользователей {} и {} :", friendId, userId);
        friends.addFriend(userId, friendId).ifPresent(error -> {
            throw new EntityNotFoundException(thisService, friends.getClass().getName(), error); });
    }

    /**
     * Метод удаляет пользователей из друзей друг друга.
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     */
    @Override
    public void deleteFriend(int userId, int friendId) {
        log.info("Удаление дружбы пользователей {} и {} :", friendId, userId);
        friends.deleteFriend(userId, friendId).ifPresent(error -> {
                    throw new EntityNotFoundException(thisService,friends.getClass().getName(), error); });
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param id ID нужного пользователя
     * @return список ID друзей
     */
    @Override
    public List<User> getFriends(int id) {
        log.info("Получение списка друзей пользователя:");
        String error = String.format(
                "Ошибка при получении списка друзей пользователя: друг ID %d не найден на сервисе!", id);
        users.getUser(id).orElseThrow(() -> new EntityNotFoundException(thisService, users.getClass().getName(),
                String.format("Пользователь ID %d не найден на сервисе!", id)));
        List<User> list = new ArrayList<>();
        for (Integer i : friends.getFriends(id)) {
            Optional<User> user = users.getUser(i);
            User orElseThrow = user.orElseThrow(() -> new EntityNotFoundException(
                    thisService, friends.getClass().getName(), error));
            list.add(orElseThrow);
        }
        return list;
    }

    /**
     * Метод возвращает список совместных друзей пользователя и его друга
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     * @return список общих друзей
     */
    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        log.info("Получение списка общих друзей двух пользователей:");
        String message = String.format("Пользователь ID %d и/или ID %d не найдены!", userId, friendId);
        var frendsIdSet = friends.getCommonFriends(userId, friendId);
        return frendsIdSet.stream()
                .map(users::getUser)
                .map(user -> user.orElseThrow(() ->
                        new EntityNotFoundException(thisService, users.getClass().getName(), message)))
                .collect(Collectors.toList());
    }

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    @Override
    public User createUser(User user) {
        log.info("Создание записи о пользователе и его регистрация на сервисе: {} :", user);
        var result = users.createUser(user).orElseThrow(() -> new EntityAlreadyExistsException(
                thisService, users.getClass().getName(),
                "Пользователь уже был создан и зарегистрирован на сервисе."));
        likes.registerUser(user.getId()).ifPresent(errorMessage -> {
            throw new InternalServiceException(thisService, likes.getClass().getName(), errorMessage);
        });
        return result;
    }

    /**
     * Метод обновляет в списке пользователей фильмотеки существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить, поиск производится по ID
     * @return обновленный пользователь
     */
    @Override
    public User updateUser(User user) {
        log.info("Обновление записи о пользователе {} :", user);
        return users.updateUser(user).orElseThrow(() -> new EntityNotFoundException(
                thisService, users.getClass().getName(), "Запись о пользователе на сервисе не найдена"));
    }

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список всех пользователей, может быть пустым
     */
    @Override
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
    @Override
    public User getUser(int userId) {
        log.info("Получение записи о пользователе ID {} :", userId);
        return users.getUser(userId).orElseThrow(() -> new EntityNotFoundException(
                thisService, users.getClass().getName(),
                String.format("Получить запись о пользователе не удалось, пользователь с ID %d не найден!", userId)));
    }

}

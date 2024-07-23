package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;

public interface BaseUserService {
    /**
     * Метод создает запрос на дружбу, или подтверждает уже имеющийся запрос.
     *
     * @param userId   ID пользователя
     * @param friendId ID добавляемого в друзья пользователя
     */
    void addFriend(int userId, int friendId);

    /**
     * Метод удаляет пользователей из друзей друг друга.
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     */
    void deleteFriend(int userId, int friendId);

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param id ID нужного пользователя
     * @return список ID друзей
     */
    List<User> getFriends(int id);

    /**
     * Метод возвращает список совместных друзей пользователя и его друга
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     * @return список общих друзей
     */
    List<User> getCommonFriends(int userId, int friendId);

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    User createUser(User user);

    /**
     * Метод обновляет в списке пользователей фильмотеки существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить, поиск производится по ID
     * @return обновленный пользователь
     */
    User updateUser(User user);

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список всех пользователей, может быть пустым
     */
    List<User> getAllUsers();

    /**
     * Метод возвращает пользователя по его ID
     *
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    User getUser(int userId);
}

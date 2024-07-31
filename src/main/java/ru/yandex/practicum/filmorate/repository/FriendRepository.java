package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;

/**
 * Интерфейс для служб, работающих с различными комьюнити пользователей.
 */
public interface FriendRepository {

    /**
     * Метод добавляет пользователя в друзья к другому пользователю.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     */
    void addFriend(int firstUserId, int secondUserId);

    /**
     * Метод удаляет пользователя из друзей другого пользователя.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     */
    void deleteFriend(int firstUserId, int secondUserId);

    /**
     * Метод возвращает список друзей выбранного пользователя.
     *
     * @param userId ID выбранного пользователя
     * @return список друзей (может быть пустым)
     */
    List<User> getFriends(int userId);

    /**
     * Метод возвращает список общих друзей двух пользователей.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return список общих друзей (может быть пустым)
     */
    List<User> getCommonFriends(int firstUserId, int secondUserId);
}

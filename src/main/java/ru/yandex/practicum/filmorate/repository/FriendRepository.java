package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для служб, работающих с различными комьюнити пользователей.
 */
public interface FriendRepository {

    /**
     * Метод добавляет пользователя в друзья к другому пользователю.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return пустое значение если добавление успешно, иначе - текст ошибки
     */
    Optional<String> addFriend(int firstUserId, int secondUserId);

    /**
     * Метод удаляет пользователя из друзей другого пользователя.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return пустое значение если добавление успешно, иначе - текст ошибки
     */
    Optional<String> deleteFriend(int firstUserId, int secondUserId);

    /**
     * Метод возвращает список друзей выбранного пользователя.
     *
     * @param userId ID выбранного пользователя
     * @return список ID друзей (может быть пустым)
     */
    List<Integer> getFriends(int userId);

    /**
     * Метод возвращает список общих друзей двух пользователей.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return список ID общих друзей (может быть пустым)
     */
    List<Integer> getCommonFriends(int firstUserId, int secondUserId);
}

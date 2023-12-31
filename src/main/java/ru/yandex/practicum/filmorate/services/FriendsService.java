package ru.yandex.practicum.filmorate.services;

import java.util.Optional;
import java.util.Set;

/**
 * Интерфейс для служб, работающих с различными комьюнити пользователей.
 */
public interface FriendsService {
    /**
     * Метод регистрирует пользователя в FriendsService.
     *
     * @param userId ID пользователя
     * @return пустое значение - если успешно, текст ошибки - если пользователь уже зарегистрирован в службе.
     */
    Optional<String> registerUser(int userId);

    /**
     * Метод отменяет регистрацию пользователя в FriendsService
     *
     * @param userId ID пользователя
     * @return пустое значение - если успешно, текст ошибки - если пользователь не найден в службе.
     */
    Optional<String> unregisterUser(int userId);

    /**
     * Метод добавляет двух пользователей в друзья друг другу.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return пустое значение если добавление успешно, иначе - текст ошибки
     */
    Optional<String> addFriend(int firstUserId, int secondUserId);

    /**
     * Метод удаляет пользователей из друзей друг друга.
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
    Optional<Set<Integer>> getFriends(int userId);

    /**
     * Метод возвращает список общих друзей двух пользователей.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return список ID общих друзей (может быть пустым), или пустое значение - если ошибка
     */
    Optional<Set<Integer>> getCommonFriends(int firstUserId, int secondUserId);
}

package ru.yandex.practicum.filmorate.services.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.Friend;
import ru.yandex.practicum.filmorate.models.User;

import java.util.*;

/**
 * Сервис реализует хранение и обработку списков друзей всех пользователей в памяти.
 */
@Slf4j
@Component
public class FriendService implements Friend {
    /**
     * Список друзей каждого пользователя
     */
    private final Map<Integer, HashSet<Integer>> friends = new HashMap<>();

    /**
     * Метод добавляет пользователя и другого пользователя в список друзей.
     *
     * @param userId ID пользователя
     * @param friendId ID добавляемого в друзья пользователя
     */
    @Override
    public void addFriend(Integer userId, Integer friendId) {
        friends.get(userId).add(friendId);
        friends.get(friendId).add(userId);
        log.info("Пользователи ID {} и ID {} добавлены друг другу в друзья", userId, friendId);
    }

    /**
     * Метод удаляет друга из списка друзей пользователя, и ссылку на него у друга.
     *
     * @param userId ID пользователя
     * @param friendId ID друга пользователя
     */
    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        friends.get(userId).remove(friendId);
        friends.get(friendId).remove(userId);
        log.info("Пользователи ID {} и ID {} удалены друг у друга из друзей", userId, friendId);
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param userId ID нужного пользователя
     * @return список ID друзей
     */
    @Override
    public List<Integer> getFriends(Integer userId) {
        log.info("Получен список друзей пользователя ID {}", userId);
        return new ArrayList<>(friends.get(userId));
    }

    /**
     * Метод возвращает список общих друзей пользователя и его друга
     *
     * @param userId ID пользователя
     * @param friendId ID друга пользователя
     * @return список ID общих друзей
     */
    @Override
    public List<Integer> getCommonFriends(Integer userId, Integer friendId) {
        Set<Integer> intersection = new HashSet<>(friends.get(userId));
        intersection.retainAll(friends.get(friendId));
        intersection.remove(userId);
        intersection.remove(friendId);
        return new ArrayList<>(intersection);
    }
}

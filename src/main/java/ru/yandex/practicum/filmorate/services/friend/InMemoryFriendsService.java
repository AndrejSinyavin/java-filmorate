package ru.yandex.practicum.filmorate.services.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.FriendsService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Сервис реализует хранение и обработку списков друзей всех пользователей в памяти.
 */
@Slf4j
@Component
public class InMemoryFriendsService implements FriendsService {
    /**
     * Список друзей каждого пользователя
     */
    private final Map<Integer, HashSet<Integer>> friends = new HashMap<>();

    /**
     * Метод создает новую запись: ID пользователя и список ID его друзей.
     *
     * @param userId ID пользователя
     * @return true - успешно, false - пользователь уже существует
     */
    @Override
    public boolean registerNewUser(int userId) {
        if (friends.containsKey(userId)) {
            log.warn("Пользователь с ID {} уже существует!", userId);
            return false;
        } else {
            friends.put(userId, new HashSet<>());
            log.info("Пользователь с ID {} записан в список учета друзей", userId);
            return true;
        }
    }

    /**
     * Метод удаляет пользователя, его список друзей и ссылки на него у друзей.
     *
     * @param userId ID удаляемого пользователя
     * @return true, если операция успешно выполнена, false - пользователь не найден
     */
    @Override
    public boolean unregisterUser(int userId) {
        var oldValue = friends.remove(userId);
        if (oldValue != null) {
            oldValue.forEach(id -> friends.get(id).remove(userId));
            log.info("Пользователь ID {} удален из списка учета друзей", userId);
            return true;
        } else {
            log.warn("Пользователь ID {} не найден!", userId);
            return false;
        }
    }

    /**
     * Метод добавляет пользователя и другого пользователя в друзья.
     *
     * @param thisId  ID пользователя
     * @param otherId ID добавляемого в друзья пользователя
     * @return true, если операция выполнена, false - если пользователи не найдены
     */
    @Override
    public boolean addFriend(int thisId, int otherId) {
        var userFriendList = friends.get(thisId);
        var otherFriendList = friends.get(otherId);
        if (userFriendList == null || otherFriendList == null) {
            log.warn("Пользователь ID {} и/или ID {} не найдены!", thisId, otherId);
            return false;
        } else {
            userFriendList.add(otherId);
            otherFriendList.add(thisId);
            log.info("Пользователи ID {} и ID {} добавлены друг другу в друзья", thisId, otherId);
            return true;
        }
    }

    /**
     * Метод удаляет друга из списка друзей пользователя, и ссылку на него у друга.
     *
     * @param thisId  ID пользователя
     * @param otherId ID друга пользователя
     * @return true, если операция выполнена, false - если пользователи не найдены
     */
    @Override
    public boolean deleteFriend(int thisId, int otherId) {
        var thisFriendList = friends.get(thisId);
        var otherFriendList = friends.get(otherId);
        if (thisFriendList == null || otherFriendList == null) {
            log.warn("Пользователь ID {} и/или ID {} не найдены!", thisId, otherId);
            return false;
        } else {
            thisFriendList.remove(otherId);
            otherFriendList.remove(thisId);
            log.info("Пользователи ID {} и ID {} удалены из друзей", thisId, otherId);
            return true;
        }
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param userId  ID нужного пользователя
     * @return список ID друзей, может быть пустым
     */
    @Override
    public Set<Integer> getFriends(int userId) {
        log.info("Получен список друзей пользователя ID {}", userId);
        return friends.get(userId);
    }

    /**
     * Метод возвращает список общих друзей двух пользователей.
     *
     * @param thisId  ID пользователя
     * @param otherId ID друга пользователя
     * @return список ID общих друзей (может быть пустым), или null - если хотя бы один пользователь не найден
     */
    @Override
    public Set<Integer> getCommonFriends(int thisId, int otherId) {
        if (friends.containsKey(thisId) && friends.containsKey(otherId)) {
            Set<Integer> intersection = new HashSet<>(friends.get(thisId));
            intersection.retainAll(friends.get(otherId));
            intersection.remove(thisId);
            intersection.remove(otherId);
            log.info("Получен список общих друзей");
            return intersection;
        } else {
            log.warn("Пользователь ID: {} или {} не найдены!", thisId, otherId);
            return null;
        }
    }
}

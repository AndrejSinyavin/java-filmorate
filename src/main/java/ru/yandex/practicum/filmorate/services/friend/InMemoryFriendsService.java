package ru.yandex.practicum.filmorate.services.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.FriendsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.*;

/**
 * Сервис реализует хранение и обработку в памяти списков друзей для каждого пользователя.
 */
@Slf4j
@Component
@Valid
public class InMemoryFriendsService implements FriendsService {
    private static final String ID_ERROR = "ID может быть только положительным значением";
    /**
     * Список друзей каждого пользователя.
     */
    private final Map<Integer, HashSet<Integer>> friends = new HashMap<>();

    /**
     * Метод регистрирует ID пользователя в FriendsService.
     *
     * @param id ID пользователя
     * @return пустое значение - успешно, текст ошибки - если пользователь уже зарегистрирован.
     */
    @Override
    public Optional<String> registerUser(@Positive(message = ID_ERROR) int id) {
        if (friends.containsKey(id)) {
            return Optional.of(String.format("Пользователь с ID %d уже был зарегистрирован в FriendsService!", id));
        } else {
            friends.put(id, new HashSet<>());
            log.info("Пользователь с ID {} зарегистрирован в FriendsService", id);
            return Optional.empty();
        }
    }

    /**
     * Метод удаляет ID пользователя из FriendsService, его список друзей и ссылки на него у друзей.
     *
     * @param userId ID удаляемого пользователя
     * @return пустое значение - успешно, текст ошибки - если пользователь не найден в FriendsService
     */
    @Override
    public Optional<String> unregisterUser(@Positive(message = ID_ERROR) int userId) {
        var oldValue = friends.remove(userId);
        if (Objects.isNull(oldValue)) {
            return Optional.of(String.format("Пользователь ID %d не найден в FriendsService!", userId));
        } else {
            oldValue.forEach(id -> friends.get(id).remove(userId));
            log.info("Пользователь ID {} удален из FriendsService", userId);
            return Optional.empty();
        }
    }

    /**
     * Метод добавляет пользователя и другого пользователя в друзья.
     *
     * @param thisId  ID пользователя
     * @param otherId ID добавляемого в друзья пользователя
     * @return пустое значение - успешно, текст ошибки - если какой-либо пользователь не найден в FriendsService
     */
    @Override
    public Optional<String> addFriend(@Positive(message = ID_ERROR) int thisId,
                                      @Positive(message = ID_ERROR) int otherId) {
        var userFriendList = friends.get(thisId);
        var otherFriendList = friends.get(otherId);
        if (Objects.isNull(userFriendList) || Objects.isNull(otherFriendList)) {
            return Optional.of(String.format(
                    "Пользователь ID %d и/или ID %d не найдены  FriendsService!", thisId, otherId));
        } else {
            userFriendList.add(otherId);
            otherFriendList.add(thisId);
            log.info("Пользователи ID {} и ID {} добавлены друг другу в друзья", thisId, otherId);
            return Optional.empty();
        }
    }

    /**
     * Метод удаляет друзей из совместного френд-листа.
     *
     * @param thisId  ID пользователя
     * @param otherId ID друга пользователя
     * @return пустое значение - успешно, иначе текст ошибки
     */
    @Override
    public Optional<String> deleteFriend(@Positive(message = ID_ERROR) int thisId,
                                         @Positive(message = ID_ERROR) int otherId) {
        var thisFriendList = friends.get(thisId);
        var otherFriendList = friends.get(otherId);
        if (Objects.isNull(thisFriendList) || Objects.isNull(otherFriendList)) {
            return Optional.of(String.format(
                    "Пользователь ID %d и/или ID %d не найдены в  FriendsService!", thisId, otherId));
        } else if (thisFriendList.remove(otherId) & otherFriendList.remove(thisId)) {
            return Optional.of(String.format("Пользователи ID %d и ID %d удалены из друзей", thisId, otherId));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param userId ID нужного пользователя
     * @return список ID друзей (может быть пустым) или пустое значение, если ошибка
     */
    @Override
    public Optional<Set<Integer>> getFriends(@Positive(message = ID_ERROR) int userId) {
        log.info("Получен список друзей пользователя ID {}", userId);
        return Optional.of(friends.get(userId));
    }

    /**
     * Метод возвращает список общих друзей двух пользователей.
     *
     * @param thisId  ID пользователя
     * @param otherId ID друга пользователя
     * @return список ID общих друзей (может быть пустым), или пустое значение - если кто-то из пользователей не найден
     */
    @Override
    public Optional<Set<Integer>> getCommonFriends(@Positive(message = ID_ERROR) int thisId,
                                                   @Positive(message = ID_ERROR) int otherId) {
        if (friends.containsKey(thisId) && friends.containsKey(otherId)) {
            Set<Integer> intersection = new HashSet<>(friends.get(thisId));
            intersection.retainAll(friends.get(otherId));
            intersection.remove(thisId);
            intersection.remove(otherId);
            log.info("Получен список общих друзей");
            return Optional.of(intersection);
        } else {
            log.warn("Пользователь ID: {} и/или {} не найдены!", thisId, otherId);
            return Optional.empty();
        }
    }
}

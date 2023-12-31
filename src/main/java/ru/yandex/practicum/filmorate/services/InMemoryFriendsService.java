package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
     * @return пустое значение - если успешно, текст ошибки - если пользователь уже зарегистрирован.
     */
    @Override
    public Optional<String> registerUser(@Positive(message = ID_ERROR) int id) {
        if (friends.containsKey(id)) {
            return Optional.of(String.format(
                    "Пользователь с ID %d уже был зарегистрирован в %s!", id, this.getClass().getName()));
        } else {
            friends.put(id, new HashSet<>());
            log.info("Пользователь с ID {} зарегистрирован в {}", id, this.getClass().getName());
            return Optional.empty();
        }
    }

    /**
     * Метод удаляет ID пользователя из FriendsService, его список друзей и ссылки на него у друзей.
     *
     * @param userId ID удаляемого пользователя
     * @return пустое значение - если успешно, текст ошибки - если пользователь не найден в FriendsService
     */
    @Override
    public Optional<String> unregisterUser(@Positive(message = ID_ERROR) int userId) {
        var deletedUserFriends = friends.remove(userId);
        if (deletedUserFriends == null) {
            return Optional.of(String.format("Пользователь ID %d не найден в %s!", userId, this.getClass().getName()));
        } else {
            deletedUserFriends.forEach(friendId -> friends.get(friendId).remove(userId));
            log.info("Пользователь ID {} удален из {}", userId, this.getClass().getName());
            return Optional.empty();
        }
    }

    /**
     * Метод добавляет пользователя и другого пользователя в друзья.
     *
     * @param firstUserId  ID пользователя
     * @param secondUserId ID добавляемого в друзья пользователя
     * @return пустое значение - если успешно, текст ошибки - если какой-либо пользователь не найден в FriendsService
     */
    @Override
    public Optional<String> addFriend(@Positive(message = ID_ERROR) int firstUserId,
                                      @Positive(message = ID_ERROR) int secondUserId) {
        var firstUserFriendList = friends.get(firstUserId);
        var secondUserFriendList = friends.get(secondUserId);
        if (firstUserFriendList == null || secondUserFriendList == null) {
            return Optional.of(String.format("Пользователь ID %d и/или ID %d не найдены в %s!",
                    firstUserId, secondUserId, this.getClass().getName()));
        } else {
            firstUserFriendList.add(secondUserId);
            secondUserFriendList.add(firstUserId);
            log.info("Пользователи ID {} и ID {} добавлены друг другу в друзья", firstUserId, secondUserId);
            return Optional.empty();
        }
    }

    /**
     * Метод удаляет друзей из совместного френд-листа.
     *
     * @param thisId  ID пользователя
     * @param otherId ID друга пользователя
     * @return пустое значение - если успешно, иначе текст ошибки
     */
    @Override
    public Optional<String> deleteFriend(@Positive(message = ID_ERROR) int thisId,
                                         @Positive(message = ID_ERROR) int otherId) {
        var thisFriendList = friends.get(thisId);
        var otherFriendList = friends.get(otherId);
        if (thisFriendList == null || otherFriendList == null) {
            return Optional.of(String.format(
                    "Пользователь ID %d и/или ID %d не найдены в %s!", thisId, otherId, this.getClass().getName()));
        } else if (thisFriendList.remove(otherId) & otherFriendList.remove(thisId)) {
            return Optional.empty();
        } else {
            return Optional.of(String.format("Ошибка при удалении ID %d или ID %d из друзей", thisId, otherId));
        }
    }

    /**
     * Метод возвращает список друзей выбранного пользователя.
     *
     * @param userId ID выбранного пользователя
     * @return список ID друзей (может быть пустым)
     */
    @Override
    public Optional<Set<Integer>> getFriends(@Positive(message = ID_ERROR) int userId) {
        log.info("Получен список друзей пользователя ID {}", userId);
        return Optional.of(friends.get(userId));
    }

    /**
     * Метод возвращает список общих друзей двух пользователей.
     *
     * @param firstUserId  ID первого пользователя
     * @param secondUserId ID второго пользователя
     * @return список ID общих друзей (может быть пустым), или пустое значение - если кто-то из пользователей не найден
     */
    @Override
    public Optional<Set<Integer>> getCommonFriends(@Positive(message = ID_ERROR) int firstUserId,
                                                   @Positive(message = ID_ERROR) int secondUserId) {
        if (friends.containsKey(firstUserId) && friends.containsKey(secondUserId)) {
            Set<Integer> commonFriendsId = new HashSet<>(friends.get(firstUserId));
            commonFriendsId.retainAll(friends.get(secondUserId));
            commonFriendsId.remove(firstUserId);
            commonFriendsId.remove(secondUserId);
            log.info("Получен список общих друзей");
            return Optional.of(commonFriendsId);
        } else {
            log.warn("Пользователь ID: {} и/или {} не найдены в {}!",
                    firstUserId, secondUserId, this.getClass().getName());
            return Optional.empty();
        }
    }
}

package ru.yandex.practicum.filmorate.interfaces;

import java.util.Optional;
import java.util.Set;

/**
 * Интерфейс для служб, работающих с различными комьюнити пользователей.
 */
public interface FriendsService {
    Optional<String> registerUser(int userId);

    Optional<String> unregisterUser(int userId);

    Optional<String> addFriend(int thisId, int otherId);

    Optional<String> deleteFriend(int thisId, int otherId);

    Optional<Set<Integer>> getFriends(int userId);

    Optional<Set<Integer>> getCommonFriends(int thisId, int otherId);
}

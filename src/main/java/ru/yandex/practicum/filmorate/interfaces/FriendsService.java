package ru.yandex.practicum.filmorate.interfaces;

import java.util.Set;

public interface FriendsService {
    boolean registerUser(int userId);

    boolean unregisterUser(int userId);

    boolean addFriend(int thisId, int otherId);

    boolean deleteFriend(int thisId, int otherId);

    Set<Integer> getFriends(int userId);

    Set<Integer> getCommonFriends(int thisId, int otherId);
}

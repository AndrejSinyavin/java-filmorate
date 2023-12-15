package ru.yandex.practicum.filmorate.interfaces;

import java.util.List;

public interface Friend {
    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    List<Integer> getFriends(Integer userId);

    List<Integer> getCommonFriends(Integer userId, Integer friendId);
}

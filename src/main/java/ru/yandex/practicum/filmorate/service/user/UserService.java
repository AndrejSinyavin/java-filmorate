package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;
    private final Map<Integer, Set<Integer>> friends = storage.getFriends();

    public void addFriend(int userId, int friendId) {
        friends.get(userId).add(friendId);
        friends.get(friendId).add(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        friends.get(userId).remove(friendId);
        friends.get(friendId).remove(userId);
    }

    public List<User> getFriends(int userId) {
        return friends.get(userId).stream()
                .map((Integer index) -> storage.getUsers().get(index))
                .collect(Collectors.toList());
    }
}

package ru.yandex.practicum.filmorate.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserServiceInternalException;
import ru.yandex.practicum.filmorate.interfaces.FriendsService;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис содержит логику работы с пользователями
 */
@Service
@RequiredArgsConstructor
public class UserService {
    /**
     * Подключение сервиса работы с пользователями.
     */
    private final UserStorage users;
    /**
     * Подключение сервиса работы с друзьями.
     */
    private final FriendsService friends;

    /**
     * Метод добавляет двух пользователей друг другу в друзья.
     *
     * @param userId ID пользователя
     * @param friendId ID добавляемого в друзья пользователя
     */
    public void addFriend(int userId, int friendId) {
        if (!friends.addFriend(userId, friendId)) {
            throw new UserNotFoundException("Указан неверный ID у одного или более пользователей!");
        }
    }

    /**
     * Метод удаляет пользователей из друзей.
     *
     * @param userId ID пользователя
     * @param friendId ID друга пользователя
     */
    public void deleteFriend(int userId, int friendId) {
        if (!friends.deleteFriend(userId, friendId)) {
            throw new UserNotFoundException("Указан неверный ID у одного или более пользователей!");
        }
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param userId ID нужного пользователя
     * @return список ID друзей
     */
    public List<User> getFriends(int userId) {
        if (users.getUser(userId) != )
        return friends.getFriends(userId).stream()
                .map(users::getUser)
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает список общих друзей пользователя и его друга
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     * @return список ID общих друзей
     */
    public List<User> getCommonFriends(int userId, int friendId) {
        var frendsIdSet = friends.getCommonFriends(userId, friendId);
        frendsIdSet.stream()
                .map(id -> {});
        return
    }

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    public User createUser(User user) {
        users.createUser(user);
        if (friends.createNewUser(user.getId())) {
            return user;
        } else {
            throw new UserServiceInternalException("Ошибка синхронизации сервисов!",
                    "Пользователь зарегистрирован на сервисе, но не зарегистрирован в службе взаимодействия друзей, " +
                            "обратитесь в службу технической поддержки!");
        }
    }

    /**
     * Метод обновляет в списке пользователей фильмотеки существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить, поиск производится по ID
     * @return обновленный пользователь
     */
    public User updateUser(User user) {
        return users.updateUser(user);
    }

    /**
     * Метод удаляет пользователя из фильмотеки.
     *
     * @param userId удаляемый пользователь
     * @return удаленный пользователь
     */
    public User deleteUser(int userId) {
        User deletedUser = users.deleteUser(userId);
        friends.getFriends(userId)
                .forEach(friendId -> friends.deleteFriend(friendId, userId));
        return deletedUser;
    }

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список всех пользователей, может быть пустым
     */
    public List<User> getAllUsers() {
        return users.getAllUsers();
    }

    /**
     * Метод возвращает пользователя по его ID
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    public User getUser(int userId) {
        return users.getUser(userId);
    }

}

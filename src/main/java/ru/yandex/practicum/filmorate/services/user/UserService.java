package ru.yandex.practicum.filmorate.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
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
        return friends.getCommonFriends(userId, friendId);
    }

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    public User createUser(User user) {
        return users.createUser(user);
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
     * @param user удаляемый пользователь
     * @return удаленный пользователь
     */
    public User deleteUser(User user) {
        User deletedUser = users.deleteUser(user);
        Integer deletedUserId = deletedUser.getId();
        friends.getFriends(deletedUserId)
                .forEach(friendId -> friends.deleteFriend(friendId, deletedUserId));
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
    public User getUser(Integer userId) {
        return users.getUser(userId);
    }

}

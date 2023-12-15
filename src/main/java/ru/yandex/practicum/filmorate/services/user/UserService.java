package ru.yandex.practicum.filmorate.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.interfaces.Friend;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.friend.FriendService;

import java.util.List;

/**
 * Сервис взаимодействия контроллера с сервисами работы с пользователями
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserStorage, Friend {
    /**
     * Подключение сервиса работы с пользователями.
     */
    private final UserStorage users;
    /**
     * Подключение сервиса работы с друзьями.
     */
    private final FriendService friends;

    /**
     * Метод добавляет пользователя и другого пользователя в список друзей.
     *
     * @param userId ID пользователя
     * @param friendId ID добавляемого в друзья пользователя
     */
    @Override
    public void addFriend(Integer userId, Integer friendId) {
        friends.addFriend(userId, friendId);
    }

    /**
     * Метод удаляет друга из списка друзей пользователя, и ссылку на него у друга.
     *
     * @param userId ID пользователя
     * @param friendId ID друга пользователя
     */
    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        friends.deleteFriend(userId, friendId);
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param userId ID нужного пользователя
     * @return список ID друзей
     */
    @Override
    public List<Integer> getFriends(Integer userId) {
        return friends.getFriends(userId);
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
        return friends.getCommonFriends(userId, friendId);
    }

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    @Override
    public User createUser(User user) {
        return users.createUser(user);
    }

    /**
     * Метод обновляет в списке пользователей фильмотеки существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить, поиск производится по ID
     * @return обновленный пользователь
     */
    @Override
    public User updateUser(User user) {
        return users.updateUser(user);
    }

    /**
     * Метод удаляет пользователя из фильмотеки.
     *
     * @param user удаляемый пользователь
     * @return удаленный пользователь
     */
    @Override
    public User deleteUser(User user) {
        User deletedUser = users.deleteUser(user);
        Integer deletedUserId = deletedUser.getId();
        friends.getFriends(deletedUserId)
                .forEach((Integer friendId) -> friends.deleteFriend(friendId, deletedUserId));
        return deletedUser;
    }

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список всех пользователей, может быть пустым
     */
    @Override
    public List<User> getAllUsers() {
        return users.getAllUsers();
    }

    /**
     * Метод возвращает пользователя по его ID
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    @Override
    public User getUser(Integer userId) {
        return users.getUser(userId);
    }


}

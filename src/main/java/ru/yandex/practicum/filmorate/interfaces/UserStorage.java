package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.models.User;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserStorage {

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    User createUser(User user);

    /**
     * Метод обновляет в списке пользователей фильмотеки существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить, поиск производится по ID
     * @return обновленный пользователь
     */
    User updateUser(User user);

    /**
     * Метод удаляет пользователя из фильмотеки.
     *
     * @param user удаляемый пользователь
     */
    User deleteUser(User user);

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список пользователей, может быть пустым
     */
    List<User> getAllUsers();

    public User getUser(Integer userId);

}

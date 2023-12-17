package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

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
    User deleteUser(int userId);

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список пользователей, может быть пустым
     */
    List<User> getAllUsers();

    public User getUser(int userId);

}

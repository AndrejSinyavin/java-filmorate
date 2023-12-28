package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для служб, работающих с пользователями фильмотеки.
 */
public interface UserStorage {

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    Optional<User> createUser(User user);

    /**
     * Метод обновляет в списке пользователей фильмотеки существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить, поиск производится по ID
     * @return обновленный пользователь
     */
    Optional<User> updateUser(User user);

    /**
     * Метод удаляет пользователя из фильмотеки.
     *
     * @param userId ID удаляемого пользователя
     */
    boolean deleteUser(int userId);

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список пользователей, может быть пустым
     */
    Optional<List<User>> getAllUsers();

    /**
     * Метод получает учетную запись пользователя из сервиса
     *
     * @param userId ID пользователя
     * @return ссылка на полученный аккаунт пользователя
     */
    Optional<User> getUser(int userId);

}

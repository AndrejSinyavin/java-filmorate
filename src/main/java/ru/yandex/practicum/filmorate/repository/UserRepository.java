package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для служб, работающих с пользователями фильмотеки.
 */
public interface UserRepository {

    /**
     * Метод создает в списке пользователей фильмотеки нового пользователя.
     *
     * @param user запись о пользователе, которую нужно создать
     * @return эта же запись с зарегистрированным новым ID
     */
    Optional<User> createUser(User user);

    /**
     * Метод обновляет в списке пользователей фильмотеки существующего пользователя.
     *
     * @param user запись о пользователе, которую нужно найти и обновить, поиск производится по ID
     * @return обновленная запись о пользователе, если она уже существовала; иначе пустое значение
     */
    Optional<User> updateUser(User user);

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список пользователей, может быть пустым
     */
    List<User> getAllUsers();

    /**
     * Метод возвращает запись о пользователе по его ID
     *
     * @param userId ID искомого пользователя
     * @return запись о пользователе; либо пустое значение, если не найден
     */
    Optional<User> getUser(int userId);

}

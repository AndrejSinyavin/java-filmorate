package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для служб, работающих с пользователями фильмотеки.
 */
public interface UserStorage {

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
     * Метод удаляет пользователя из фильмотеки.
     *
     * @param userId ID удаляемого пользователя
     * @return запись о пользователе, который был удален; пустое значение - если запись не была найдена
     */
    Optional<User> deleteUser(int userId);

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список пользователей, может быть пустым
     */
    Optional<List<User>> getAllUsers();

    /**
     * Метод возвращает запись о пользователе по его ID
     *
     * @param userId ID искомого пользователя
     * @return запись о пользователе; либо пустое значение, если не найден
     */
    Optional<User> getUser(int userId);

}

package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Хранилище и бизнес-логика работы со списком клиентов фильмотеки в памяти.
 */
@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    /**
     * Список пользователей хранилища фильмотеки.
     */
    final Map<Integer, User> users = new HashMap<>();
    /**
     * Список друзей каждого пользователя
     */
    @Getter
    final Map<Integer, Set<Integer>> friends = new HashMap<>();
    /**
     * Счетчик ID для зарегистрированных пользователей хранилища фильмотеки
     */
    private int countId;

    /**
     * Метод создает в списке пользователей хранилища нового пользователя с уникальным ID.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    @Override
    public @NotNull User createUser(@NotNull User user) {
        int id = newId();
        user.setId(id);
        users.put(id, user);
        log.info("Пользователь успешно добавлен в список пользователей фильмотеки: {}", user);
        return user;
    }

    /**
     * Метод обновляет в списке пользователей хранилища существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить, поиск производится по ID
     * @return обновленный пользователь
     */
    @Override
    public @NotNull User updateUser(@NotNull User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
            log.info("Пользователь успешно обновлен в список пользователей фильмотеки: {}", user);
            return user;
        } else {
            String message = "Пользователь не найден!";
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }

    /**
     * Метод возвращает список всех пользователей фильмотеки из хранилища.
     *
     * @return список пользователей, может быть пустым
     */
    @Override
    public @NotNull List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Метод авто-генерации ID для создаваемых в фильмотеке пользователей
     *
     * @return новый ID
     */
    private int newId() {
        return ++countId;
    }

}

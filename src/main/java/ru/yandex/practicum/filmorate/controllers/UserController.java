package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.user.UserService;

import javax.validation.Valid;
import java.util.List;

import static ru.yandex.practicum.filmorate.services.validation.AdditionalUserValidator.validateUser;

/**
 * Контроллер обработки REST-запросов для работы со списком клиентов фильмотеки.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public final class UserController {
    private final UserService users;

    /**
     * Эндпоинт обрабатывает запрос на создание нового пользователя фильмотеки.
     *
     * @param user пользователь, полученный из тела запроса
     * @return этот же пользователь с уже зарегистрированным ID в фильмотеке
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        users.createUser(user);
        log.info("Пользователь успешно добавлен в список пользователей: {}", user);
        return users.getUser(user.getId());
    }

    /**
     * Эндпоинт обрабатывает запрос на обновление в фильмотеке существующего пользователя.
     *
     * @param user пользователь, полученный из тела запроса
     * @return обновленный пользователь
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validateUser(user);
        users.updateUser(user);
        log.info("Пользователь успешно обновлен в списке пользователей");
        return user;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение всех пользователей фильмотеки
     *
     * @return список всех пользователей, может быть пустым
     */
    @GetMapping
    public List<User> getUsers() {
        var result = users.getAllUsers();
        log.info("Получен список всех пользователей");
        return result;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение пользователя фильмотеки по его ID.
     *
     * @return пользователь
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        var result = users.getUser(id);
        log.info("Получен пользователь");
        return result;
    }

    /**
     * Эндпоинт обрабатывает запрос на добавление в друзья двух пользователей.
     *
     * @param id первый пользователь
     * @param friendId второй пользователь
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        users.addFriend(id, friendId);
        log.info("Пользователи добавлены в друзья");
    }

    /**
     * Эндпоинт обрабатывает запрос на удаление двух пользователей из друзей.
     *
     * @param id первый пользователь
     * @param friendId второй пользователь
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        users.deleteFriend(id, friendId);
        log.info("Пользователи удалены из друзей");
    }

    /**
     * Эндпоинт обрабатывает запрос на получение списка всех ID друзей пользователя.
     *
     * @param id ID пользователя
     * @return список ID всех друзей
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        var result = users.getFriends(id);
        log.info("Получен список друзей пользователя ID {}", id);
        return result;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        var result = users.getCommonFriends(id, otherId);
        log.info("Получен список общих друзей двух пользователей: ID {} и ID {}", id, otherId);
        return result;
    }
}

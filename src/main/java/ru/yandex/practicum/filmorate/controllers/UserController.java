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
 * Контроллер обработки REST-запросов для работы с клиентами фильмотеки.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public final class UserController {
    /**
     * Подключение сервиса работы с пользователями UserService.
     */
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
        log.info("Запрос");
        log.info("==> POST {}", user);
        validateUser(user);
        users.createUser(user);
        log.info("<== Пользователь успешно добавлен в список пользователей сервиса: {}", user);
        return user;
    }

    /**
     * Эндпоинт обрабатывает запрос на обновление в фильмотеке существующего пользователя.
     *
     * @param user пользователь, полученный из тела запроса
     * @return обновленный пользователь
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос");
        log.info("==> PUT {}", user);
        validateUser(user);
        users.updateUser(user);
        log.info("<== Пользователь успешно обновлен в списке пользователей сервиса: {}", user);
        return user;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение всех пользователей фильмотеки
     *
     * @return список всех пользователей, может быть пустым
     */
    @GetMapping
    public List<User> getUsers() {
        log.info("Запрос");
        log.info("==> GET получить список всех пользователей");
        var result = users.getAllUsers();
        log.info("<== Список всех пользователей сервиса");
        return result;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение пользователя фильмотеки по его ID.
     *
     * @return пользователь
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Запрос");
        log.info("==> GET получить пользователя");
        var result = users.getUser(id);
        log.info("<== Пользователь сервиса: {}", result);
        return result;
    }

    /**
     * Эндпоинт обрабатывает запрос на добавление в друзья двух пользователей.
     *
     * @param id       первый пользователь
     * @param friendId второй пользователь
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос");
        log.info("==> PUT добавить в друзья");
        users.addFriend(id, friendId);
        log.info("<== Пользователи сервиса ID {} и ID {} добавлены в друзья", id, friendId);
    }

    /**
     * Эндпоинт обрабатывает запрос на удаление пользователей из друзей друг у друга.
     *
     * @param id       первый пользователь
     * @param friendId второй пользователь
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос");
        log.info("==> DELETE удалить из друзей");
        users.deleteFriend(id, friendId);
        log.info("<== Пользователи сервиса ID {} и ID {} удалены друг у друга из друзей", id, friendId);
    }

    /**
     * Эндпоинт обрабатывает запрос на получение списка всех друзей пользователя.
     *
     * @param id ID пользователя
     * @return список всех друзей
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Запрос");
        log.info("==> GET получить список друзей пользователя");
        var result = users.getFriends(id);
        log.info("<== Список всех друзей пользователя сервиса ID {}", id);
        return result;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение списка всех общих друзей двух пользователей.
     *
     * @param id      ID одного пользователя
     * @param otherId ID другого пользователя
     * @return список всех общих друзей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Запрос");
        log.info("==> GET получить список совместных друзей двух пользователей");
        var result = users.getCommonFriends(id, otherId);
        log.info("<==  Список общих друзей пользователей сервиса ID {} и ID {}", id, otherId);
        return result;
    }
}

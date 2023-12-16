package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.yandex.practicum.filmorate.services.validation.AdditionalUserValidator.validateUser;

/**
 * Контроллер обработки REST-запросов для работы со списком клиентов фильмотеки.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
public final class UserController {
    private final UserService users;

    /**
     * Эндпоинт обрабатывает запрос на создание нового пользователя фильмотеки.
     *
     * @param user пользователь, полученный из тела запроса
     * @return этот же пользователь с уже зарегистрированным ID в фильмотеке
     */
    @PostMapping
    public @NotNull User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        users.createUser(user);
        log.info("Пользователь успешно добавлен в список пользователей фильмотеки: {}", user);
        return user;
    }

    /**
     * Эндпоинт обрабатывает запрос на обновление в фильмотеке существующего пользователя.
     *
     * @param user пользователь, полученный из тела запроса
     * @return обновленный пользователь
     */
    @PutMapping
    public @NotNull User updateUser(@Valid @RequestBody User user) {
        validateUser(user);
        users.updateUser(user);
        log.info("Пользователь успешно обновлен в списке пользователей фильмотеки");
        return user;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение всех пользователей фильмотеки
     *
     * @return список всех пользователей, может быть пустым
     */
    @GetMapping
    public @NotNull List<User> getUsers() {
        log.info("Сервер вернул список всех пользователей фильмотеки");
        return users.getAllUsers();
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
    }

    /**
     * Эндпоинт обрабатывает запрос на получение списка всех ID друзей пользователя.
     *
     * @param id ID пользователя
     * @return список ID всех друзей
     */
    @GetMapping("/{id}/friends")
    public List<Integer> getFriends(@PathVariable int id) {
        return users.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<Integer> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return users.getCommonFriends(id, otherId);
    }
}

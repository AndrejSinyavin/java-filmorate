package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.service.BaseUserService;

import java.util.List;

import static ru.yandex.practicum.filmorate.validate.ValidateExtender.validateUser;

/**
 * Контроллер обработки REST-запросов для работы с пользователями фильмотеки.
 */
@Slf4j
@Valid
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final String idError = "Ошибка! ID может быть только положительным значением";
    /**
     * Подключение сервиса работы с пользователями.
     */
    private final BaseUserService users;

    /**
     * Endpoint обрабатывает запрос на создание нового пользователя фильмотеки.
     *
     * @param user пользователь, полученный из тела запроса
     * @return этот же пользователь с уже зарегистрированным ID в фильмотеке
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос ==> POST {}", user);
        validateUser(user);
        users.createUser(user);
        log.info("Ответ <== 201 Created. Пользователь успешно добавлен в список пользователей сервиса: {}", user);
        return user;
    }

    /**
     * Endpoint обрабатывает запрос на обновление в фильмотеке существующего пользователя.
     *
     * @param user пользователь, полученный из тела запроса
     * @return обновленный пользователь
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос ==> PUT {}", user);
        validateUser(user);
        users.updateUser(user);
        log.info("Ответ <== 200 Ok. Пользователь успешно обновлен в списке пользователей сервиса: {}", user);
        return user;
    }

    /**
     * Endpoint обрабатывает запрос на получение всех пользователей фильмотеки
     *
     * @return список всех пользователей, может быть пустым
     */
    @GetMapping
    public List<User> getUsers() {
        log.info("Запрос ==> GET получить список всех пользователей");
        var result = users.getAllUsers();
        log.info("Ответ <== 200 Ok. Список всех пользователей сервиса");
        return result;
    }

    /**
     * Endpoint обрабатывает запрос на получение пользователя фильмотеки по его ID.
     *
     * @return пользователь
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable @Positive(message = idError) int id) {
        log.info("Запрос ==> GET получить пользователя");
        var result = users.getUser(id);
        log.info("Ответ <== 200 Ok. Пользователь сервиса: {}", result);
        return result;
    }

    /**
     * Endpoint обрабатывает запрос на добавление в друзья двух пользователей.
     *
     * @param id пользователь, создающий запрос
     * @param friendId пользователь, добавляемый в друзья
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable @Positive(message = idError) int id,
            @PathVariable @Positive(message = idError) int friendId) {
        log.info("Запрос ==> PUT добавить в друзья");
        users.addFriend(id, friendId);
        log.info("Ответ <== 200 Ok. Создан запрос от пользователя ID {} на добавление в друзья пользователя ID {}  ",
                id, friendId);
    }

    /**
     * Endpoint обрабатывает запрос на удаление пользователей из друзей друг у друга.
     *
     * @param id  пользователь, создающий запрос
     * @param friendId пользователь, которого нужно удалить из списка друзей
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable @Positive(message = idError) int id,
            @PathVariable @Positive(message = idError) int friendId) {
        log.info("Запрос ==> DELETE удалить из друзей");
        users.deleteFriend(id, friendId);
        log.info("Ответ <== 200 Ok. Создан запрос на удаление пользователя ID {} из списка друзей ID {}",
                friendId, id);
    }

    /**
     * Endpoint обрабатывает запрос на получение списка всех друзей пользователя.
     *
     * @param id целевой пользователь
     * @return список всех его друзей
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable @Positive(message = idError) int id) {
        log.info("Запрос ==> GET получить список друзей пользователя");
        var result = users.getFriends(id);
        log.info("Ответ <== 200 Ok. Список всех друзей пользователя сервиса ID {}", id);
        return result;
    }

    /**
     * Endpoint обрабатывает запрос на получение списка всех общих друзей двух пользователей.
     *
     * @param id первый пользователь
     * @param otherId второй пользователь
     * @return список всех общих друзей двух пользователей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable @Positive(message = idError) int id,
            @PathVariable @Positive(message = idError) int otherId) {
        log.info("Запрос ==> GET получить список совместных друзей двух пользователей");
        var result = users.getCommonFriends(id, otherId);
        log.info("Ответ <==  200 Ok. Список общих друзей пользователей сервиса ID {} и ID {}", id, otherId);
        return result;
    }

}

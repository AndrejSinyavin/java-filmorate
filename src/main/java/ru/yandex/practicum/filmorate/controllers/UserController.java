package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.EntityValidateException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.yandex.practicum.filmorate.services.validation.ValidateExtender.validateUser;

/**
 * Контроллер обработки REST-запросов для работы с клиентами фильмотеки.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final String ID_ERROR = "ID может быть только положительным значением";
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
        log.info("Запрос ==> POST {}", user);
        String errorMessage = validateUser(user);
        if (!errorMessage.isEmpty()) {
            throw new EntityValidateException("Сервис дополнительной валидации", "Валидация запроса в контроллере " +
                    this.getClass().getCanonicalName(), errorMessage);
        }
        users.createUser(user);
        log.info("Ответ <== 201 Created. Пользователь успешно добавлен в список пользователей сервиса: {}", user);
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
        log.info("Запрос ==> PUT {}", user);
        validateUser(user);
        users.updateUser(user);
        log.info("Ответ <== 200 Ok. Пользователь успешно обновлен в списке пользователей сервиса: {}", user);
        return user;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение всех пользователей фильмотеки
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
     * Эндпоинт обрабатывает запрос на получение пользователя фильмотеки по его ID.
     *
     * @return пользователь
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable @Positive(message = ID_ERROR) int id) {
        log.info("Запрос ==> GET получить пользователя");
        var result = users.getUser(id);
        log.info("Ответ <== 200 Ok. Пользователь сервиса: {}", result);
        return result;
    }

    /**
     * Эндпоинт обрабатывает запрос на добавление в друзья двух пользователей.
     *
     * @param id первый пользователь
     * @param friendId второй пользователь
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable @Positive(message = ID_ERROR) int id,
            @PathVariable @Positive(message = ID_ERROR) int friendId) {
        log.info("Запрос ==> PUT добавить в друзья");
        users.addFriend(id, friendId);
        log.info("Ответ <== 200 Ok. Пользователи сервиса ID {} и ID {} добавлены в друзья", id, friendId);
    }

    /**
     * Эндпоинт обрабатывает запрос на удаление пользователей из друзей друг у друга.
     *
     * @param id первый пользователь
     * @param friendId второй пользователь
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable @Positive(message = ID_ERROR) int id,
            @PathVariable @Positive(message = ID_ERROR) int friendId) {
        log.info("Запрос ==> DELETE удалить из друзей");
        users.deleteFriend(id, friendId);
        log.info("Ответ <== 200 Ok. Пользователи сервиса ID {} и ID {} удалены друг у друга из друзей", id, friendId);
    }

    /**
     * Эндпоинт обрабатывает запрос на получение списка всех друзей пользователя.
     *
     * @param id ID пользователя
     * @return список всех друзей
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable @Positive(message = ID_ERROR) int id) {
        log.info("Запрос ==> GET получить список друзей пользователя");
        var result = users.getFriends(id);
        log.info("Ответ <== 200 Ok. Список всех друзей пользователя сервиса ID {}", id);
        return result;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение списка всех общих друзей двух пользователей.
     *
     * @param id ID одного пользователя
     * @param otherId ID другого пользователя
     * @return список всех общих друзей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable @Positive(message = ID_ERROR) int id,
            @PathVariable @Positive(message = ID_ERROR) int otherId) {
        log.info("Запрос ==> GET получить список совместных друзей двух пользователей");
        var result = users.getCommonFriends(id, otherId);
        log.info("Ответ <==  200 Ok. Список общих друзей пользователей сервиса ID {} и ID {}", id, otherId);
        return result;
    }

}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.yandex.practicum.filmorate.service.misc.AdditionalValidator.validateUser;

/**
 * Контроллер обработки HTTP-запросов для работы со списком клиентов фильмотеки.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public final class UserController {

    /**
     * Эндпоинт обрабатывает запрос на создание нового пользователя фильмотеки.
     *
     * @param user пользователь, полученный из тела запроса
     * @return этот же пользователь с уже зарегистрированным ID в фильмотеке
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public @NotNull User createUser(@Valid @RequestBody User user) {
        validateUser(user);

        log.info("Пользователь успешно добавлен в список пользователей фильмотеки: {}", user);
        return user;
    }

    /**
     * Эндпоинт обрабатывает запрос на обновление в фильмотеке существующего пользователя.
     *
     * @param user пользователь, полученный из тела запроса
     * @return обновленный пользователь
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public @NotNull User updateUser(@Valid @RequestBody User user) {
        validateUser(user);

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
        return ;
    }

}

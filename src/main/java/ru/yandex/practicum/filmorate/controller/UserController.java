package ru.yandex.practicum.filmorate.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.service.ValidateSettings.LIFE_TIME;

/**
 * Контроллер обработки HTTP-запросов для работы со списком клиентов фильмотеки.
 * Содержит в себе список имеющихся клиентов. Позволяет: добавить пользователя в список клиентов,
 * обновить уже имеющегося клиента, получить список всех клиентов
 */
@Log4j2
@RestController
@RequestMapping("/users")
public final class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int countId;

    /**
     * Метод создает нового пользователя фильмотеки.
     *
     * @param user пользователь, полученный из тела запроса и прошедший первичную валидацию
     * @return этот же пользователь с уже зарегистрированным ID в фильмотеке
     * @throws ResponseStatusException если не пройдена дополнительная валидация
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@Valid @RequestBody User user) {
        try {
            validateUser(user);
            int id = newId();
            user.setId(id);
            users.put(id, user);
            log.info("Пользователь успешно добавлен в каталог: {}", user);
        } catch (ValidateUserException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return user;
    }

    /**
     * Метод обновляет в фильмотеке существующего пользователя.
     *
     * @param user пользователь, полученный из тела запроса и прошедший первичную валидацию
     * @return этот же пользователь с уже зарегистрированным ID в фильмотеке
     * @throws ResponseStatusException если пользователь не найден или не прошел дополнительную валидацию
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@Valid @RequestBody User user) {
        try {
            validateUser(user);
            int id = user.getId();
            if (users.containsKey(id)) {
                users.put(id, user);
                log.info("Пользователь успешно обновлен в каталоге");
                return user;
            } else {
                log.warn("Пользователь не найден!");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден!");
            }
        } catch (ValidateUserException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Метод возвращает всех пользователей фильмотеки
     *
     * @return список всех пользователей, может быть пустым
     */
    @GetMapping
    public List<@NotNull User> getUsers() {
        log.info("Сервер вернул список всех пользователей");
        return new ArrayList<>(users.values());
    }

    /**
     * Метод выполняет дополнительную валидацию запроса и корректирует его, если необходимо, либо отклоняет.
     *
     * @param user пользователь, которому нужно провести дополнительные проверки
     * @throws ValidateUserException если дополнительные проверки не пройдены
     */
    private static void validateUser(@NonNull User user) throws ValidateUserException {
        log.info("Валидация характеристик пользователя:");
        String name = user.getName();
        LocalDate birthday = LocalDate.parse(user.getBirthday());
        if (birthday.isAfter(LocalDate.now()) && birthday.getYear() > LIFE_TIME) {
            throw new ValidateUserException("Некорректная дата рождения пользователя!");
        }
        if (name == null || name.isBlank()) {
            log.warn("Имя пользователя не задано, ему присвоено содержимое поля логин!");
            user.setName(user.getLogin());
        }
        log.info("Ok.");
    }

    /**
     * Метод авто-генерации ID для создаваемых в фильмотеке пользователей
     *
     * @return новый ID
     */
    private int newId() {
        return ++countId;
    }

    /**
     * Исключение для метода {@link UserController#validateUser(User)}
     */
    private static class ValidateUserException extends Exception {
        public ValidateUserException(String message) {
            super(message);
        }
    }
}

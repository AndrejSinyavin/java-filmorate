package ru.yandex.practicum.filmorate.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.rmi.ServerException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.model.Properties.LIFE_TIME;

@RestController
@RequestMapping(path = "/users",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public final class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int countId;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ServerException {
        try {
            validateUser(user);
            int id = newId();
            if (!users.containsKey(id)) {
                users.put(id, user);
                log.info("Пользователь успешно добавлен в каталог");
                return user;
            } else {
                log.warn("Пользователь с таким ID {} уже есть в каталоге!", id);
                throw new ServerException("Пользователь с таким ID " + id + " уже есть в каталоге!");
            }
        } catch (ValidateUserException e) {
            log.error(e.getMessage());
            throw new ServerException("Сервер не выполнил запрос - недопустимые характеристики пользователя");
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ServerException {
        try {
            validateUser(user);
            int id = newId();
            if (users.put(id, user) == null) {
                log.info("Пользователь успешно добавлен в каталог");
            } else {
                log.info("Пользователь успешно обновлен в каталоге");
            }
            return user;
        } catch (ValidateUserException e) {
            log.error(e.getMessage());
            throw new ServerException("Сервер не выполнил запрос - недопустимые характеристики пользователя");
        }
    }

    @GetMapping
    public Map<Integer, User> getUsers() {
        log.info("Сервер вернул список всех фильмов");
        return users;
    }

    private static void validateUser(User user) throws ValidateUserException {
        log.info("Валидация характеристик пользователя:");
        String name = user.getName();
        if (name.isBlank() || name.isEmpty()) {
            log.warn("Имя пользователя не задано, будет использован логин!");
            user.setName(user.getLogin());
        }
        LocalDate birthday = LocalDate.parse(user.getBirthday());
        if (birthday.isBefore(LocalDate.now()) && birthday.getYear() > LIFE_TIME) {
            throw new ValidateUserException("Некорректная дата рождения пользователя");
        }
        log.info("Ok.");
    }

    private int newId() {
        return ++countId;
    }

    private static class ValidateUserException extends Throwable {
        public ValidateUserException(String s) {
        }
    }
}

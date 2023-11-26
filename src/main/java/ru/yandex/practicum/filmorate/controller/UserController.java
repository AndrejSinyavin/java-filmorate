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

import static ru.yandex.practicum.filmorate.model.Properties.LIFE_TIME;

@Log4j2
@RestController
@RequestMapping("/users")
public final class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int countId;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@Valid @RequestBody User user) {
        try {
            validateUser(user);
            int id = newId();
            user.setId(id);
            users.put(id, user);
            log.info("Пользователь успешно добавлен в каталог");
            return user;
        } catch (ValidateUserException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

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
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (ValidateUserException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public List<@NotNull User> getUsers() {
        log.info("Сервер вернул список всех пользователей");
        return new ArrayList<>(users.values());
    }

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

    private int newId() {
        return ++countId;
    }

    public static class ValidateUserException extends Throwable {
        public ValidateUserException(String s) {
        }
    }
}

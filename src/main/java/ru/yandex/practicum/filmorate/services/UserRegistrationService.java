package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Сервис регистрации пользователей фильмотеки.
 */
@Slf4j
@Valid
@Component
public class UserRegistrationService implements RegistrationService<User> {
    /**
     * Счетчик ID для регистрации пользователей в фильмотеке
     */
    private int userId;

    /**
     * Метод регистрации пользователя в фильмотеке
     */
    @Override
    public int register(@NotNull(message = "Пользователь не должен быть null") User user) {
        user.setId(++userId);
        log.info("Пользователь зарегистрирован, ID = {}", userId);
        return userId;
    }
}

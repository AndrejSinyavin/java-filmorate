package ru.yandex.practicum.filmorate.services.registration;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.RegistrationService;
import ru.yandex.practicum.filmorate.models.User;

@Component
public final class UserRegistrationService implements RegistrationService<User> {
    /**
     * Счетчик ID для регистрации пользователей в фильмотеке
     */
    private static int userId;

    /**
     * Метод регистрации пользователя в фильмотеке
     */
    @Override
    public int register(User user) {
        user.setId(++userId);
        return userId;
    }
}

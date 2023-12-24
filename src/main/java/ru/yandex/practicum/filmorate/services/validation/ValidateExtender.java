package ru.yandex.practicum.filmorate.services.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.exceptions.EntityValidateException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.services.misc.ApplicationSettings.MAX_AGE;

/**
 * Сервисный класс с дополнительными методами валидации данных.
 */
@Slf4j
public final class ValidateExtender {

    private ValidateExtender() {
    }

    /**
     * Метод выполняет дополнительную валидацию запроса и корректирует его, если необходимо, либо отклоняет.
     *
     * @param user пользователь, которому нужно провести дополнительные проверки
     * @throws EntityValidateException если дополнительные проверки не пройдены
     */
    public static void validateUser(@NonNull User user) {
        log.info("Валидация характеристик пользователя:");
        String name = user.getName();
        LocalDate birthday = LocalDate.parse(user.getBirthday());
        if (birthday.isAfter(LocalDate.now()) && birthday.getYear() > MAX_AGE) {
            String errorMessage = "Недопустимая дата рождения пользователя!";
            log.error(errorMessage);
            throw new EntityValidateException("Сервис дополнительной валидации ->", "Валидация запроса в контроллере ->",
                    errorMessage);
        }
        if (name == null || name.isBlank()) {
            log.warn("Имя пользователя не задано, ему присвоено содержимое поля 'логин'!");
            user.setName(user.getLogin());
        }
        log.info("Ok.");
    }
}

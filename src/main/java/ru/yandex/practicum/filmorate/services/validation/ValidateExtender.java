package ru.yandex.practicum.filmorate.services.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
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
     * @return текст ошибки, если дополнительные проверки не пройдены, иначе пустая строка
     */
    public static String validateUser(@NonNull User user) {
        log.info("Дополнительная валидация характеристик пользователя:");
        String name = user.getName();
        int age = LocalDate.now().getYear() - user.getBirthday().getYear();
        if (age > MAX_AGE) {
            return String.format("Возраст пользователя не может быть более %d", MAX_AGE);
        }
        if (name == null || name.isBlank()) {
            log.warn("Имя пользователя не задано, ему присвоено содержимое поля 'логин'!");
            user.setName(user.getLogin());
        }
        log.info("Ok.");
        return "";
    }
}

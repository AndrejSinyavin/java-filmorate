package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.entity.User;

import java.time.LocalDate;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.MAX_AGE;

/**
 * Сервисный класс с дополнительными методами валидации данных.
 */
@Slf4j
public final class ValidateExtender {
    private static final String ENTITY_ERROR = "Пользователь не должен быть null";

    private ValidateExtender() {
    }

    /**
     * Метод выполняет дополнительную валидацию пользователя и корректирует его поля, если необходимо.
     *
     * @param user пользователь, которому нужно провести дополнительные проверки
     * @return список сообщений об ошибках, если дополнительные проверки не пройдены, иначе пустое значение
     */
    public static Optional<String> validateUser(@NotNull(message = ENTITY_ERROR) User user) {
        log.info("Дополнительная валидация характеристик пользователя:");
        String errorMessage = "";
        String name = user.getName();
        int age = LocalDate.now().getYear() - user.getBirthday().getYear();
        if (age > MAX_AGE) {
            errorMessage = errorMessage + String.format("Возраст пользователя не может быть более %d\n", MAX_AGE);
            log.error(errorMessage);
        }
        if (name == null || name.isBlank()) {
            log.warn("Имя пользователя не было задано, ему присвоено содержимое поля 'логин'");
            user.setName(user.getLogin());
        }
        if (errorMessage.isEmpty()) {
            log.info("Ok.");
            return Optional.empty();
        } else {
            return Optional.of(errorMessage);
        }
    }
}

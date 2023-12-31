package ru.yandex.practicum.filmorate.validations;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.misc.ApplicationSettings.MAX_AGE;

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
     * @return сообщение об ошибке, если дополнительные проверки не пройдены, иначе пустое значение
     */
    public static Optional<String> validateUser(@NotNull(message = ENTITY_ERROR) User user) {
        log.info("Дополнительная валидация характеристик пользователя:");
        String name = user.getName();
        int age = LocalDate.now().getYear() - user.getBirthday().getYear();
        if (age > MAX_AGE) {
            return Optional.of(String.format("Возраст пользователя не может быть более %d", MAX_AGE));
        }
        if (name == null || name.isBlank()) {
            log.warn("Имя пользователя не было задано, ему присвоено содержимое поля 'логин'");
            user.setName(user.getLogin());
        }
        log.info("Ok.");
        return Optional.empty();
    }
}

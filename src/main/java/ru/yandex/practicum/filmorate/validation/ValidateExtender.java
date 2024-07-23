package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.User;

import java.time.LocalDate;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.MAX_AGE;

/**
 * Сервисный класс с дополнительными методами валидации данных.
 */
@Slf4j
@Validated
public final class ValidateExtender {
    private static final String USER_ERROR = "Пользователь не должен быть null";
    private static final String FILM_ERROR = "Фильм не должен быть null";

    private ValidateExtender() {
    }

    /**
     * Метод выполняет дополнительную валидацию пользователя и корректирует его поля, если необходимо.
     *
     * @param user пользователь, которому нужно провести дополнительные проверки
     * @return список сообщений об ошибках, если дополнительные проверки не пройдены, иначе пустое значение
     */
    public static Optional<String> validateUser(@NotNull(message = USER_ERROR) User user) {
        log.info("Дополнительная валидация характеристик пользователя:");
        String errorMessage = "";
        String name = user.getName();
        int age = LocalDate.now().getYear() - user.getBirthday().getYear();
        if (age > MAX_AGE) {
            errorMessage = errorMessage + String.format("Возраст пользователя не может быть более %d; ", MAX_AGE);
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

    /**
     * Метод выполняет дополнительную валидацию фильма и корректирует его поля, если необходимо.
     *
     * @param film фильм, которому нужно провести дополнительные проверки
     * @return список сообщений об ошибках, если дополнительные проверки не пройдены, иначе пустое значение
     */
    public static Optional<String> validateFilm(@NotNull(message = FILM_ERROR) Film film) {
        log.info("Дополнительная валидация характеристик фильма:");
        String errorMessage = "";
        var genres = film.getGenres();
        if (genres != null && !genres.isEmpty() && genres.stream().anyMatch(id -> id <= 0)) {
            errorMessage = errorMessage +
                    "В списке жанров найден недопустимый ID жанра - все ID должны быть положительными значениями; ";
            log.error(errorMessage);
        }
        if (errorMessage.isEmpty()) {
            log.info("Ok.");
            return Optional.empty();
        } else {
            return Optional.of(errorMessage);
        }
    }
}

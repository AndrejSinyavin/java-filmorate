package ru.yandex.practicum.filmorate.services.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.services.validation.ValidateSettings.MAX_AGE;

@Slf4j
public final class AdditionalUserValidator {

    private AdditionalUserValidator() {
    }

    /**
     * Метод выполняет дополнительную валидацию запроса и корректирует его, если необходимо, либо отклоняет.
     *
     * @param user пользователь, которому нужно провести дополнительные проверки
     * @throws ResponseStatusException с кодом {@link HttpStatus#BAD_REQUEST} если дополнительные проверки не пройдены
     */
    public static void validateUser(@NonNull User user) {
        log.info("Валидация характеристик пользователя:");
        //ToDo проверить, что пользователь с таким email уже существует
        String name = user.getName();
        LocalDate birthday = LocalDate.parse(user.getBirthday());
        if (birthday.isAfter(LocalDate.now()) && birthday.getYear() > MAX_AGE) {
            String errorMessage = "Некорректная дата рождения пользователя!";
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        if (name == null || name.isBlank()) {
            log.warn("Имя пользователя не задано, ему присвоено содержимое поля логин!");
            user.setName(user.getLogin());
        }
        log.info("Ok.");
    }
}

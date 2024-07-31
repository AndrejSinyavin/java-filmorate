package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.VALID_RELEASE_DATE;

/**
 * Реализация пользовательской аннотации {@link Release}
 */
@Slf4j
public class ReleaseValidator implements ConstraintValidator<Release, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            log.warn("Дата релиза фильма не задана, допустимо");
            return true;
        } else {
            return date.isAfter(VALID_RELEASE_DATE.minusDays(1));
        }
    }
}
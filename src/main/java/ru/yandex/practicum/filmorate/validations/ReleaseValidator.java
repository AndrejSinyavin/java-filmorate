package ru.yandex.practicum.filmorate.validations;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.misc.ApplicationSettings.VALID_RELEASE_DATE;

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

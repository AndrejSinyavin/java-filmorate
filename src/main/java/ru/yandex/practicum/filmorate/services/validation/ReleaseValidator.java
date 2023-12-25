package ru.yandex.practicum.filmorate.services.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.services.misc.ApplicationSettings.VALID_RELEASE_DATE;

/**
 * Реализация кастомной аннотации {@link Release}
 */
@Slf4j
public class ReleaseValidator implements ConstraintValidator<Release, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (Objects.isNull(date)) {
            log.warn("Дата релиза фильма не задана");
            return true;
        } else {
            return date.isAfter(VALID_RELEASE_DATE.minusDays(1));
        }
    }
}

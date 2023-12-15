package ru.yandex.practicum.filmorate.services.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import java.time.format.DateTimeParseException;

import static java.time.LocalDate.parse;
import static ru.yandex.practicum.filmorate.services.validation.ValidateSettings.VALID_RELEASE_DATE;

@Slf4j
public class ReleaseValidator implements ConstraintValidator<Release, String> {

    @Override
    public boolean isValid(@NotNull String s, ConstraintValidatorContext context) {
        if (s == null || s.isEmpty()) {
            log.warn("Дата релиза не задана");
            return true;
        } else {
            try {
                return parse(s).isAfter(VALID_RELEASE_DATE.minusDays(1));
            } catch (DateTimeParseException e) {
                log.warn("Дата релиза не соответствует паттерну даты");
                return false;
            }
        }
    }
}

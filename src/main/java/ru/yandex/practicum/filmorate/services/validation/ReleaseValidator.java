package ru.yandex.practicum.filmorate.services.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import java.time.format.DateTimeParseException;

import static java.time.LocalDate.parse;
import static ru.yandex.practicum.filmorate.services.misc.ValidateSettings.VALID_RELEASE_DATE;

@Slf4j
public class ReleaseValidator implements ConstraintValidator<Release, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null || s.isEmpty()) {
            log.warn("Дата релиза фильма не задана");
            return true;
        } else {
            try {
                return parse(s).isAfter(VALID_RELEASE_DATE.minusDays(1));
            } catch (DateTimeParseException e) {
                log.warn("Дата релиза фильма не соответствует установленному паттерну даты yyyy.mm.dd");
                return false;
            }
        }
    }
}

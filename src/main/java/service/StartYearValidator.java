package service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.model.Properties.VALID_RELEASE_DATE;

public class StartYearValidator implements ConstraintValidator<StartYear, String> {

    @Override
    public boolean isValid(@NotNull String s, ConstraintValidatorContext context) {
        if (s == null) {
            return false;
        } else {
            return LocalDate.parse(s).isAfter(VALID_RELEASE_DATE);
        }
    }
}

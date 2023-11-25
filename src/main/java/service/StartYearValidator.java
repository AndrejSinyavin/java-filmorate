package service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.model.Properties.VALID_RELEASE_DATE;

public class StartYearValidator implements ConstraintValidator<StartYear, String> {

    @Override
    public void initialize(StartYear constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return LocalDate.parse(s).isAfter(VALID_RELEASE_DATE);
    }
}

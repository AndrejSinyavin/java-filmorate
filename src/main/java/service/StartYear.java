package service;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.yandex.practicum.filmorate.model.Properties.MAX_DESCRIPTION_LENGTH;

@Constraint(validatedBy = StartYearValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StartYear {
    String message() default "Год релиза не может быть меньше " + MAX_DESCRIPTION_LENGTH;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

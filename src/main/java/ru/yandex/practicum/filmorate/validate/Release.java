package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Пользовательская аннотация для валидации года релиза фильма.
 */
@Constraint(validatedBy = ReleaseValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Release {
    String text = "Год релиза не может быть ранее 28 декабря 1895," +
            " либо дата релиза не соответствует паттерну даты";

    String message() default text;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

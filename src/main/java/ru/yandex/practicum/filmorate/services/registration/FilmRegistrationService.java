package ru.yandex.practicum.filmorate.services.registration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.RegistrationService;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * Сервис регистрации фильмов в фильмотеке.
 */
@Slf4j
@Component
@Valid
public class FilmRegistrationService implements RegistrationService<Film> {
    /**
     * Счетчик ID для регистрации фильма в фильмотеке
     */
    private int filmId;

    /**
     * Метод регистрации фильма в фильмотеке
     */
    @Override
    public @Positive int register(@NonNull Film film) {
        film.setId(++filmId);
        log.info("Фильм зарегистрирован, ID = {}", filmId);
        return filmId;
    }
}

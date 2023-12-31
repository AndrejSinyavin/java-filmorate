package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.constraints.NotNull;

/**
 * Сервис регистрации фильмов в фильмотеке.
 */
@Slf4j
@Component
public class FilmRegistrationService implements RegistrationService<Film> {
    /**
     * Счетчик ID для регистрации фильма в фильмотеке
     */
    private int filmId;

    /**
     * Метод регистрации фильма в фильмотеке
     */
    @Override
    public int register(@NotNull(message = "Пользователь не должен быть null") Film film) {
        film.setId(++filmId);
        log.info("Фильм зарегистрирован, ID = {}", filmId);
        return filmId;
    }
}

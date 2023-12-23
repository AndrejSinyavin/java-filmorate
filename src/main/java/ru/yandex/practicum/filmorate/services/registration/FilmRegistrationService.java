package ru.yandex.practicum.filmorate.services.registration;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.RegistrationService;
import ru.yandex.practicum.filmorate.models.Film;

/**
 * Сервис регистрации фильмов в фильмотеке.
 */
@Component
public final class FilmRegistrationService implements RegistrationService<Film> {
    /**
     * Счетчик ID для регистрации фильма в фильмотеке
     */
    private static int filmId;

    /**
     * Метод регистрации фильма в фильмотеке
     */
    @Override
    public int register(Film film) {
        film.setId(++filmId);
        return filmId;
    }
}

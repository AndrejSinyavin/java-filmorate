package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.rmi.ServerException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.model.Properties.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.model.Properties.VALID_RELEASE_DATE;

@RestController
@RequestMapping(path = "/films",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public final class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int countId;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws ServerException {
        try {
            validateFilm(film);
            int id = newId();
            film.setId(id);
            films.put(id, film);
            log.info("Фильм успешно добавлен в каталог, ID = {}", id);
            return film;
        } catch (ValidateFilmException e) {
            log.error(e.getMessage());
            throw new ServerException("Сервер не выполнил запрос - недопустимые характеристики фильма");
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ServerException {
        try {
            validateFilm(film);
            int id = film.getId();
            if (films.containsKey(id)) {
                films.put(id, film);
                log.info("Фильм успешно обновлен в каталоге");
                return film;
            } else {
                id = newId();
                film.setId(id);
                films.put(id, film);
                log.info("Фильм успешно добавлен в каталог");
                return film;
            }
        } catch (ValidateFilmException e) {
            log.error(e.getMessage());
            throw new ServerException("Сервер не выполнил запрос - недопустимые характеристики фильма");
        }
    }

    @GetMapping
    public Map<Integer, Film> getFilms() {
        log.info("Сервер вернул список всех фильмов");
        return films;
    }

    private static void validateFilm(@NonNull Film film) throws ValidateFilmException {
        log.info("Валидация характеристик фильма:");
        String name = film.getName();
        if (name.isEmpty() || name.isBlank()) {
            throw new ValidateFilmException("Ошибка! Отсутствует название фильма");
        } else if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidateFilmException("Ошибка! Описание фильма слишком большое (более 200 символов)");
        } else if ((LocalDate.parse(film.getReleaseDate())).isBefore(VALID_RELEASE_DATE)) {
            throw new ValidateFilmException("Ошибка! Недопустимая дата релиза - до " + VALID_RELEASE_DATE);
        } else if (film.getDuration() <= 0) {
            throw new ValidateFilmException("Ошибка! Продолжительность фильма не может быть отрицательной или равна 0");
        } else {
            log.info("Ok.");
        }
    }

    private int newId() {
        return ++countId;
    }

    private static final class ValidateFilmException extends Exception {
        public ValidateFilmException(String s) {
        }
    }
}

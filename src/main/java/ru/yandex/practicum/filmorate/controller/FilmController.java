package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.model.Properties.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.model.Properties.VALID_RELEASE_DATE;

@Log4j2
@RestController
@RequestMapping("/films")
public final class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int countId;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        try {
            validateFilm(film);
            int id = newId();
            film.setId(id);
            films.put(id, film);
            log.info("Фильм успешно добавлен в каталог, ID = {}", id);
            return film;
        } catch (ValidateFilmException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        try {
            validateFilm(film);
            int id = film.getId();
            if (films.containsKey(id)) {
                films.put(id, film);
                log.info("Фильм успешно обновлен в каталоге");
                return film;
            } else {
                log.warn("Фильм не найден!");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (ValidateFilmException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Сервер вернул список всех фильмов");
        return new ArrayList<>(films.values());
    }

    private static void validateFilm(@NonNull Film film) throws ValidateFilmException {
        log.info("Валидация характеристик фильма:");
        String name = film.getName();
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidateFilmException("Описание фильма слишком большое (более 200 символов)");
        } else if ((LocalDate.parse(film.getReleaseDate())).isBefore(VALID_RELEASE_DATE)) {
            throw new ValidateFilmException("Недопустимая дата релиза - до " + VALID_RELEASE_DATE);
        } else if (film.getDuration() <= 0) {
            throw new ValidateFilmException("Продолжительность фильма не может быть отрицательной или равной 0");
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

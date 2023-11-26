package ru.yandex.practicum.filmorate.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/films")
public final class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int countId;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film createFilm(@Valid @RequestBody Film film) {
        int id = newId();
        film.setId(id);
        films.put(id, film);
        log.info("Фильм добавлен в каталог: {}", film);
        return film;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film updateFilm(@Valid @RequestBody Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            log.info("Фильм обновлен в каталоге: {}", film);
            return film;
        } else {
            log.warn("Фильм не найден!");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<@NotNull Film> getFilms() {
        ArrayList<Film> films = new ArrayList<>(this.films.values());
        log.info("Сервер вернул список всех фильмов: {}", films);
        return films;
    }

    private int newId() {
        return ++countId;
    }
}

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

/**
 * Контроллер обработки HTTP-запросов для работы с фильмотекой. Содержит в себе хранилище фильмов.
 * Позволяет: добавить фильм в фильмотеку, обновить уже добавленный, получить список имеющихся фильмов
 */
@Log4j2
@RestController
@RequestMapping("/films")
public final class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int countId;

    /**
     * Метод создает в фильмотеке новую запись "Фильм".
     *
     * @param film фильм, получаемый из тела запроса и прошедший валидацию
     * @return этот же фильм с уже зарегистрированным ID в фильмотеке
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film createFilm(@Valid @RequestBody Film film) {
        int id = newId();
        film.setId(id);
        films.put(id, film);
        log.info("Фильм добавлен в каталог: {}", film);
        return film;
    }

    /**
     * Метод обновляет в фильмотеке существующий фильм.
     *
     * @param film фильм из запроса с установленным ID, по которому ищет этот фильм в фильмотеке, и обновляет его
     * @return этот же фильм с уже зарегистрированным ID в фильмотеке
     * @throws ResponseStatusException если фильм не найден
     */
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

    /**
     * Метод возвращает все фильмы фильмотеки
     *
     * @return список всех фильмов, список может быть пустым
     */
    @GetMapping
    public List<@NotNull Film> getFilms() {
        ArrayList<Film> list = new ArrayList<>(films.values());
        log.info("Сервер вернул список всех фильмов: {}", list);
        return list;
    }

    /**
     * Метод авто-генерации ID для создаваемых в фильмотеке фильмов
     *
     * @return новый ID
     */
    private int newId() {
        return ++countId;
    }
}

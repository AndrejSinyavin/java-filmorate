package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Контроллер обработки HTTP-запросов для работы с фильмотекой.
 */
@Slf4j
@RestController
@RequestMapping("/films")
public final class FilmController {

    /**
     * Эндпоинт обрабатывает запрос на создание в фильмотеке новой записи "Фильм".
     *
     * @param film фильм, получаемый из тела запроса
     * @return созданная запись - фильм с уже зарегистрированным ID в фильмотеке
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public @NotNull Film createFilm(@Valid @RequestBody Film film) {

        log.info("Обработан запрос клиента на создание фильма в фильмотеке: {},", film);
        return film;
    }

    /**
     * Эндпоинт обрабатывает запрос на обновление в фильмотеке существующей записи "Фильм".
     *
     * @param film фильм из запроса с установленным ID, по которому ищется этот фильм в фильмотеке
     * @return обновленная запись - фильм из фильмотеки
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public @NotNull Film updateFilm(@Valid @RequestBody Film film) {

        log.info("Обработан запрос клиента на обновление фильма в фильмотеке: {},", film);
        return film;
    }

    /**
     * Эндпоинт возвращает список всех фильмов фильмотеки.
     *
     * @return список всех фильмов фильмотеки, может быть пустым
     */
    @GetMapping
    public List<@NotNull Film> getFilms() {

        log.info("Обработан запрос клиента на получение списка всех фильмов фильмотеки: {}", list);
        return list;
    }
}
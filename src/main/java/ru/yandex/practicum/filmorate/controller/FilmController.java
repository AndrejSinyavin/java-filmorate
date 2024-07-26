package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityValidateException;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.service.BaseFilmService;

import java.util.List;

/**
 * Контроллер обработки REST-запросов для работы с фильмотекой.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private static final String ID_ERROR = "ID может быть только положительным значением";
    /**
     * Подключение сервиса работы с фильмами FilmService.
     */
    private final BaseFilmService films;

    /**
     * Endpoint обрабатывает запрос на создание в фильмотеке новой записи "Фильм".
     *
     * @param film фильм, получаемый из тела запроса
     * @return созданная запись - фильм с уже зарегистрированным ID в фильмотеке
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Запрос ==> POST {}", film);
        films.createfilm(film);
        log.info("Ответ <== 201 Created. Фильм успешно добавлен в фильмотеку: {}", film);
        return film;
    }

    /**
     * Endpoint обрабатывает запрос на обновление фильма в фильмотеке.
     *
     * @param film фильм из запроса с установленным ID, по которому ищется этот фильм в фильмотеке
     * @return обновленная запись - фильм из фильмотеки
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос ==> PUT {}", film);
        films.updateFilm(film);
        log.info("Ответ <== 200 Ok. Фильм успешно обновлен в фильмотеке: {}", film);
        return film;
    }

    /**
     * Endpoint обрабатывает запрос на получение списка всех фильмов.
     *
     * @return список всех фильмов фильмотеки, может быть пустым
     */
    @GetMapping
    public List<Film> getFilms() {
        log.info("Запрос ==> GET получить список всех фильмов");
        List<Film> result = films.getFilms();
        log.info("Ответ <== 200 Ok. Список всех фильмов сервиса");
        return result;
    }

    /**
     * Endpoint обрабатывает запрос на получение фильма из фильмотеки.
     *
     * @param id ID фильма
     * @return фильм
     */
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable @Positive(message = ID_ERROR) int id) {
        log.info("Запрос ==> GET получить фильм по ID {}", id);
        Film film = films.getFilm(id);
        log.info("Ответ <== 200 Ok. Отправлен фильм: ID {}", film);
        return film;
    }

    /**
     * Endpoint обрабатывает запрос на лайк фильма пользователем.
     *
     * @param id     фильма
     * @param userId пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable @Positive(message = ID_ERROR) int id,
            @PathVariable @Positive(message = ID_ERROR) int userId) {
        log.info("Запрос ==> PUT поставить лайк фильму ID {} от пользователя {}", id, userId);
        films.addLike(id, userId);
        log.info("Ответ <== 200 Ok. Лайк поставлен");
    }

    /**
     * Endpoint обрабатывает запрос на дизлайк фильма пользователем.
     *
     * @param id фильма
     * @param userId пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(
            @PathVariable @Positive(message = ID_ERROR) int id,
            @PathVariable @Positive(message = ID_ERROR) int userId) {
        log.info("Запрос ==> DELETE удалить лайк фильму ID {} от пользователя {}", id, userId);
        films.deleteLike(id, userId);
        log.info("Ответ <== 200 Ok. Лайк удален");
    }

    /**
     * Endpoint обрабатывает запрос на получение топа рейтинга фильмов по лайкам пользователей.
     *
     * @param count размер топа рейтинга
     * @return список из фильмов в порядке понижения рейтинга
     */
    @GetMapping("/popular")
    public List<Film> getTopFilms(
            @RequestParam(name = "count", defaultValue = "10")
            @Positive(message = "Размер топа фильмов должен быть положительным значением")
            Integer count) {
        log.info("Запрос ==> GET получить топ-{} лучших фильмов", count);
        var result = films.getTopFilms(count);
        log.info("Ответ <== 200 Ok. Топ фильмотеки отправлен");
        return result;
    }

}
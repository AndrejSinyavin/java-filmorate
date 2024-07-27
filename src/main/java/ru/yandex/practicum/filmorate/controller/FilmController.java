package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.service.BaseFilmService;

import java.util.List;

/**
 * Контроллер обработки REST-запросов для работы с фильмотекой.
 */
@Slf4j
@Valid
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final String idError = "Ошибка! ID может быть только положительным значением";
    /**
     * Подключение сервиса работы с фильмами.
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
        log.info("Ответ <== 201 Created. Фильм успешно добавлен в фильмотеку {}", film);
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
        List<Film> filmList = films.getFilms();
        log.info("Ответ <== 200 Ok. Отправлен список всех фильмов сервиса {}", filmList);
        return filmList;
    }

    /**
     * Endpoint обрабатывает запрос на получение фильма из фильмотеки.
     *
     * @param id ID фильма
     * @return фильм
     */
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable @Positive(message = idError) int id) {
        log.info("Запрос ==> GET получить фильм по ID {}", id);
        Film film = films.getFilm(id);
        log.info("Ответ <== 200 Ok. Отправлен фильм ID {}", film);
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
            @PathVariable @Positive(message = idError) int id,
            @PathVariable @Positive(message = idError) int userId) {
        log.info("Запрос ==> PUT поставить лайк фильму ID {} от пользователя {}", id, userId);
        films.addLike(id, userId);
        log.info("Ответ <== 200 Ok. Лайк поставлен");
    }

    /**
     * Endpoint обрабатывает запрос на отмену лайка фильма пользователем.
     *
     * @param id фильма
     * @param userId пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(
            @PathVariable @Positive(message = idError) int id,
            @PathVariable @Positive(message = idError) int userId) {
        log.info("Запрос ==> DELETE отменить лайк фильму ID {} от пользователя {}", id, userId);
        films.deleteLike(id, userId);
        log.info("Ответ <== 200 Ok. Лайк отменен");
    }

    /**
     * Endpoint обрабатывает запрос на получение топа рейтинга фильмов по лайкам пользователей.
     *
     * @param topSize размер топа рейтинга
     * @return список из фильмов в порядке понижения рейтинга
     */
    @GetMapping("/popular")
    public List<Film> getTopFilms(
            @RequestParam(name = "topSize", defaultValue = "10")
            @Positive(message = "Размер топа фильмов должен быть положительным значением")
            Integer topSize) {
        log.info("Запрос ==> GET получить топ-{} лучших фильмов", topSize);
        var topFilms = films.getTopFilms(topSize);
        log.info("Ответ <== 200 Ok. Топ-{} фильмотеки отправлен {}", topSize, topFilms.size());
        return topFilms;
    }
}
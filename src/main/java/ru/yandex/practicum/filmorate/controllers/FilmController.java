package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.services.film.FilmService;

import javax.validation.Valid;
import java.util.List;

/**
 * Контроллер обработки REST-запросов для работы с фильмотекой.
 */
@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public final class FilmController {
    /**
     * Подключение сервиса работы с фильмами FilmService.
     */
    private final FilmService films;

    /**
     * Эндпоинт обрабатывает запрос на создание в фильмотеке новой записи "Фильм".
     *
     * @param film фильм, получаемый из тела запроса
     * @return созданная запись - фильм с уже зарегистрированным ID в фильмотеке
     */
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Запрос");
        log.info("==> POST {}", film);
        films.createfilm(film);
        log.info("<== Фильм успешно добавлен в фильмотеку: {}", film);
        return film;
    }

    /**
     * Эндпоинт обрабатывает запрос на обновление фильма в фильмотеке.
     *
     * @param film фильм из запроса с установленным ID, по которому ищется этот фильм в фильмотеке
     * @return обновленная запись - фильм из фильмотеки
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос");
        log.info("==> PUT {}", film);
        films.updateFilm(film);
        log.info("<== Фильм успешно обновлен в фильмотеке: {}", film);
        return film;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение списка всех фильмов.
     *
     * @return список всех фильмов фильмотеки, может быть пустым
     */
    @GetMapping
    public List<Film> getFilms() {
        log.info("Запрос");
        log.info("==> GET получить список всех фильмов");
        List<Film> result = films.getFilms();
        log.info("<== Список всех фильмов сервиса");
        return result;
    }

    /**
     * Эндпоинт обрабатывает запрос на получение фильма из фильмотеки.
     *
     * @param id ID фильма
     * @return фильм
     */
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Запрос");
        log.info("==> GET получить фильм по ID {}", id);
        Film film = films.getFilm(id);
        log.info("<== Отправлен фильм: ID {}", film);
        return film;
    }

    /**
     * Эндпоинт обрабатывает запрос на удаление фильма из фильмотеки.
     *
     * @param id ID фильма
     */
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        log.info("Запрос");
        log.info("==> DELETE удалить фильм по ID {}", id);
        films.deleteFilm(id);
        log.info("<== Удален фильм {}", id);
    }

    /**
     * Эндпоинт обрабатывает запрос на лайк фильма пользователем.
     *
     * @param id     фильма
     * @param userId пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос");
        log.info("==> PUT поставить лайк фильму ID {} от пользователя {}", id, userId);
        films.addLike(id, userId);
        log.info("<== Лайк поставлен");
    }

    /**
     * Эндпоинт обрабатывает запрос на дизлайк фильма пользователем.
     *
     * @param id     фильма
     * @param userId пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос");
        log.info("==> DELETE удалить лайк фильму ID {} от пользователя {}", id, userId);
        films.deleteLike(id, userId);
        log.info("<== Лайк удален");
    }

    /**
     * Эндпоинт обрабатывает запрос на получение топа рейтинга фильмов по лайкам пользователей.
     *
     * @param count размер топа рейтинга
     * @return список из фильмов в порядке понижения рейтинга
     */
    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        log.info("Запрос");
        log.info("==> GET получить топ-{} лучших фильмов", count);
        var result = films.getTopFilms(count);
        log.info("<== Топ фильмотеки отправлен");
        return result;
    }
}
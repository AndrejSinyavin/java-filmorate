package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    private final String idError = "Ошибка! ID может быть только положительным значением";
    /**
     * Подключение сервиса работы с фильмами.
     */
    private final BaseFilmService filmsService;

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
        filmsService.createfilm(film);
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
        filmsService.updateFilm(film);
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
        List<Film> filmList = filmsService.getFilms();
        log.info("Ответ <== 200 Ok. Отправлен список всех фильмов сервиса {}", filmList);
        return filmList;
    }

    /**
     * Endpoint обрабатывает запрос на получение фильма из фильмотеки.
     *
     * @param id ID фильма
     * @return фильм
     */
    @GetMapping("/{film-id}")
    public Film getFilm(@PathVariable("film-id") @Positive(message = idError) int id) {
        log.info("Запрос ==> GET получить фильм по ID {}", id);
        Film film = filmsService.getFilm(id);
        log.info("Ответ <== 200 Ok. Отправлен фильм ID {}", film);
        return film;
    }

    /**
     * Endpoint обрабатывает запрос на лайк фильма пользователем.
     *
     * @param filmId фильма
     * @param userId пользователя
     */
    @PutMapping("/{film-id}/like/{user-id}")
    public void addLike(
            @PathVariable("film-id") @Positive(message = idError) int filmId,
            @PathVariable("user-id") @Positive(message = idError) int userId) {
        log.info("Запрос ==> PUT поставить лайк фильму ID {} от пользователя {}", filmId, userId);
        filmsService.addLike(filmId, userId);
        log.info("Ответ <== 200 Ok. Лайк поставлен");
    }

    /**
     * Endpoint обрабатывает запрос на отмену лайка фильма пользователем.
     *
     * @param filmId фильма
     * @param userId пользователя
     */
    @DeleteMapping("/{film-id}/like/{user-id}")
    public void deleteLike(
            @PathVariable("film-id") @Positive(message = idError) int filmId,
            @PathVariable("user-id") @Positive(message = idError) int userId) {
        log.info("Запрос ==> DELETE отменить лайк фильму ID {} от пользователя {}", filmId, userId);
        filmsService.deleteLike(filmId, userId);
        log.info("Ответ <== 200 Ok. Лайк отменен");
    }

    /**
     * Endpoint обрабатывает запрос на получение топа рейтинга фильмов по лайкам пользователей.
     *
     * @param topSize размер топа рейтинга
     * @param genreId идентификатор жанра (необязательный параметр)
     * @param year    год релиза фильма (необязательный параметр)
     * @return список из фильмов в порядке понижения рейтинга
     */
    @GetMapping("/popular")
    public List<Film> getTopFilms(
            @RequestParam(name = "count", required = false)
            @Positive(message = "Размер топа фильмов должен быть положительным значением")
            Integer topSize,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        log.info("Запрос ==> GET получить топ-{} лучших фильмов", topSize);
        var topFilms = filmsService.getTopFilms(topSize, genreId, year);
        log.info("Ответ <== 200 Ok. Топ-{} фильмотеки отправлен {}", topSize, topFilms.size());
        return topFilms;
    }

    /**
     * Endpoint обрабатывает запрос на получение списка общих фильмов
     *
     * @param userId   идентификатор пользователя, запрашивающего информацию
     * @param friendId идентификатор пользователя, с которым необходимо сравнить список фильмов
     * @return возвращает список фильмов, отсортированных по популярности.
     */
    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam @Positive(message = idError) int userId,
                                     @RequestParam @Positive(message = idError) int friendId) {
        log.info("Запрос ==> GET получить общие фильмы пользователей с ID = {} и ID ={}", userId, friendId);
        List<Film> commonFilms = filmsService.getCommonFilms(userId, friendId);
        log.info("Ответ <== 200 Ok. Получены общие фильмы пользователей с ID = {} и ID ={}", userId, friendId);
        return commonFilms;
    }

    /**
     * Endpoint обрабатывает запрос на получение списка фильмов режиссера с вариантами сортировки результата
     *
     * @param directorId ID режиссера
     * @param sortBy     критерий сортировки
     * @return отсортированный список фильмов с этим режиссером
     */
    @GetMapping("/director/{director-id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilmsSortedByCriteria(
            @Positive(message = idError) @PathVariable("director-id") int directorId,
            @NotEmpty(message = "Ошибка! Отсутствует критерий сортировки") @RequestParam String sortBy) {
        log.info("Запрос ==> GET список фильмов режиссера ID {}, критерий сортировки {}", directorId, sortBy);
        var result = filmsService.getFilmsSortedByCriteria(directorId, sortBy);
        log.info("Ответ <== 200 Ok. Список фильмов режиссера ID {} отправлен {}", directorId, result);
        return result;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFilmById(
            @Positive(message = idError) @PathVariable("id") int id) {
        log.info("Запрос ==> DELETE на удаление фильма с  ID {}", id);
        filmsService.deleteFilm(id);
        log.info("Ответ <== 200 Ok. Фильм с ID {} удален", id);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam(value = "query", required = true) String query,
                                  @RequestParam(value = "by", required = true) String by) {
        log.info("Запрос ==> GET список фильмов по строке {}, и параметры фильтрации {}", query, by);
        return filmsService.getFilmsByTitleAndDirector(query, by);
    }
}
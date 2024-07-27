package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.service.BaseGenreService;

import java.util.List;

/**
 * Контроллер обработки REST-запросов для работы с жанрами фильмотеки.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final String idError = "Ошибка! ID может быть только положительным значением";

    /**
     * Подключение сервиса работы с жанрами.
     */
    private final BaseGenreService genreService;

    /**
     * Endpoint обрабатывает запрос на получение жанра по его ID.
     *
     * @param id жанра
     * @return {@link Genre}
     */
    @GetMapping("/{id}")
    public Genre getGenreByID(@PathVariable @Positive(message = idError) final int id) {
        log.info("Запрос ==> GET получить жанр по его ID {}", id);
        Genre genre = genreService.getGenre(id);
        log.info("Ответ <== 200 Ok. Жанр отправлен ID {} {}", id, genre);
        return genre;
    }

    /**
     * Endpoint обрабатывает запрос на получение списка всех имеющихся жанров.
     *
     * @return список из {@link Genre}
     */
    @GetMapping()
    public List<Genre> getAllGenres() {
        log.info("Запрос ==> GET получить список всех жанров ");
        var allGenres = genreService.getAllGenres();
        log.info("Ответ <== 200 Ok. Отправлен список жанров {}", allGenres);
        return allGenres;
    }
}

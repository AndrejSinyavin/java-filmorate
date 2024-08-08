package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность Film = null";
    private final String idError = "Ошибка! ID сущности может быть только положительным значением";
    /**
     * Подключение сервиса работы с режиссерами.
     */
    private final DirectorService directorService;
    private final FilmService filmService;

    /**
     * Endpoint обрабатывает запрос на создание режиссера.
     *
     * @param director режиссер, получаемый из тела запроса
     * @return режиссер с уже зарегистрированным ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody Director director) {
        log.info("Запрос ==> POST {}", director);
        directorService.createDirector(director);
        log.info("Ответ <== 201 Created. Режиссер успешно добавлен {}", director);
        return director;
    }

    /**
     * Endpoint обрабатывает запрос на обновление режиссера.
     *
     * @param director режиссер, получаемый из тела запроса
     * @return режиссер
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director update(@Valid @RequestBody() Director director) {
        log.info("Запрос ==> PUT {}", director);
        directorService.createDirector(director);
        log.info("Ответ <== 200 Ok. Режиссер успешно обновлен {}", director);
        return director;
    }

    /**
     * Endpoint обрабатывает запрос на получение режиссера.
     *
     * @param id ID режиссера
     * @return режиссер
     */
    @GetMapping("/{id}")
    public Director getById(@Positive(message = idError) @PathVariable int id) {
        log.info("Запрос ==> GET получить директора по ID {}", id);
        Director director = directorService.getDirectorById(id);
        log.info("Ответ <== 200 Ok. Отправлен директор ID {}", director);
        return director;
    }

    /**
     * Endpoint обрабатывает запрос на получение списка всех режиссеров.
     *
     * @return список всех режиссеров
     */
    @GetMapping
    public List<Director> getAll() {
        log.info("Запрос ==> GET получить список всех режиссеров");
        List<Director> directorList = directorService.getAllDirectors();
        log.info("Ответ <== 200 Ok. Отправлен список всех режиссеров {}", directorList);
        return directorList;
    }

    /**
     * Endpoint обрабатывает запрос на удаление режиссера.
     */
    @DeleteMapping("/{id}")
    public void deleteById(@Positive(message = idError) @PathVariable int id) {
        log.info("Запрос ==> DELETE удалить режиссера ID {}", id);
        directorService.deleteDirector(id);
        log.info("Ответ <== 200 Ok. Режиссеров {} удален", id);
    }
}

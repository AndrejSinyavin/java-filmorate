package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.exception.EntityValidateException;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

/**
 * Контроллер обработки REST-запросов для работы с режиссерами.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final String thisService = this.getClass().getName();
    private final String idError = "Ошибка! ID сущности может быть только положительным значением";
    /**
     * Подключение сервиса работы с режиссерами.
     */
    private final DirectorService directorService;

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
        director = directorService.createDirector(director);
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
        String name = director.getName();
        if (name == null || name.isEmpty()) {
            throw new EntityValidateException(thisService, "Создание режиссера", "Не задано ФИО режиссера");
        }
        director = directorService.updateDirector(director);
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
    public Director getById(@PathVariable @Positive(message = idError) int id) {
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
    public Collection<Director> getAll() {
        log.info("Запрос ==> GET получить список всех режиссеров");
        var directors = directorService.getAllDirectors();
        log.info("Ответ <== 200 Ok. Отправлен список всех режиссеров {}", directors);
        return directors;
    }

    /**
     * Endpoint обрабатывает запрос на удаление режиссера.
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @Positive(message = idError) int id) {
        log.info("Запрос ==> DELETE удалить режиссера ID {}", id);
        directorService.deleteDirector(id);
        log.info("Ответ <== 200 Ok. Режиссер {} удален", id);
    }
}

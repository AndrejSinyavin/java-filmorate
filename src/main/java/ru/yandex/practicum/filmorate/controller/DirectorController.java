package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

@Slf4j
@Validated
@RestController
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


}

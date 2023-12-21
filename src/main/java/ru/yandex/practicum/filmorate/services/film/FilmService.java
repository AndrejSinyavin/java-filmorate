package ru.yandex.practicum.filmorate.services.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.LikesService;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final String ERROR =
            "Сервис работы с фильмами не выполнил задачу из-за отказа в сервисе LikesService";
    /**
     * Подключение сервиса работы с фильмами.
     */
    private final FilmStorage films;
    /**
     * Подключение сервиса работы с "лайками".
     */
    private final LikesService likes;


}

package ru.yandex.practicum.filmorate.services.misc;

import java.time.LocalDate;

import static java.time.Month.DECEMBER;

/**
 * Настройки для валидации запросов
 */
public class ApplicationSettings {
    // ToDo реализовать настройки и окружение в application.properties
    // Секция настроек сервиса лайков.
    public static final boolean LIKE_PROTECTED_MODE = false;
    // Секция настроек валидации.
    public static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, DECEMBER, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final int MAX_AGE = 130;

    private ApplicationSettings() {
    }
}

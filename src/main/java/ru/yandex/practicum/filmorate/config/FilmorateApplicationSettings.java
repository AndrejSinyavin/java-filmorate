package ru.yandex.practicum.filmorate.config;

import java.time.LocalDate;

import static java.time.Month.DECEMBER;

/**
 * Настройки для валидации запросов
 */
public class FilmorateApplicationSettings {
    public static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, DECEMBER, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final int MAX_AGE = 130;

    private FilmorateApplicationSettings() {
    }
}

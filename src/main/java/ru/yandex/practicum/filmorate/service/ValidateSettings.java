package ru.yandex.practicum.filmorate.service;

import java.time.LocalDate;

import static java.time.Month.*;

/**
 * Настройки для валидации запросов
 */
public final class ValidateSettings {
    public static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, DECEMBER, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final int LIFE_TIME = 130;
}

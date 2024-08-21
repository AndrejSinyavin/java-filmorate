package ru.yandex.practicum.filmorate.config;

import java.time.LocalDate;

import static java.time.Month.DECEMBER;

/**
 * Настройки для валидации запросов, дефолтные значения из ТЗ
 */
public class FilmorateApplicationSettings {
    public static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, DECEMBER, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final int MAX_AGE = 130;
    public static final int DEFAULT_MPA_RATING = 1;
    public static final int RATING_SCALE_DIMENSION = 10;
    public static final boolean TEST_MODE = true;
    public static final double DEFAULT_RATE = 0;

    private FilmorateApplicationSettings() {
    }

    public enum DirectorSortParams { year, likes }
}

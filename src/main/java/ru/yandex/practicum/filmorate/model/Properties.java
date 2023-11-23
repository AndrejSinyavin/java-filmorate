package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import static java.time.Month.*;

public final class Properties {
    public static final LocalDate VALID_RELEASE_DATE = LocalDate.of(1895, DECEMBER, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    // Максимальный срок жизни человека
    // https://ru.wikipedia.org/wiki/Список_старейших_людей_в_мире
    public static final int LIFE_TIME = 130;
}

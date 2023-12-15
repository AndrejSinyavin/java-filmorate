package ru.yandex.practicum.filmorate.controllers;

import lombok.Data;

/**
 * Стандартный формат описания ошибок.
 */
@Data
public class ErrorResponse {
    private final String errorType;
    private final String errorDescription;
}

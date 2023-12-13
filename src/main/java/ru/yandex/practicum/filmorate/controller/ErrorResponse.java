package ru.yandex.practicum.filmorate.controller;

import lombok.Data;

/**
 * Ошибка, возвращаемая клиенту в ответе
 */
@Data
public class ErrorResponse {
    private final String errorType;
    private final String errorDescription;
}

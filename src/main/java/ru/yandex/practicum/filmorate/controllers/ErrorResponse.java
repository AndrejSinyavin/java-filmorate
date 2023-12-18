package ru.yandex.practicum.filmorate.controllers;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * API для передачи сообщений об ошибках клиенту.
 */
@Getter
@Setter
public final class ErrorResponse {
    private final Map<String, String> errors;

    public ErrorResponse(String error, String message) {
        errors = Map.of(error, message);
    }

    public ErrorResponse(Map<String, String> errors) {
        this.errors = errors;
    }
}

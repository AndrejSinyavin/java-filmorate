package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class RestControllerAdviceException extends RuntimeException {
    private final String error;
    private final String message;
}

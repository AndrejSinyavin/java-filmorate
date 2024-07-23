package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class AppException extends RuntimeException {
    private final String source;
    private final String error;
    private final String message;
}

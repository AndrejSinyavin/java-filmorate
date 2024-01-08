package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class AppException extends RuntimeException {
    private final String source;
    private final String error;
    private final String message;
}

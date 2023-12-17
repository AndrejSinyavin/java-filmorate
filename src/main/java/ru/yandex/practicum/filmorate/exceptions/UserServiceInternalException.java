package ru.yandex.practicum.filmorate.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserServiceInternalException extends RuntimeException {
    private final String error;
    private final String message;
}

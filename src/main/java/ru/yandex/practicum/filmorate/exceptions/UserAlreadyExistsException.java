package ru.yandex.practicum.filmorate.exceptions;

public final class UserAlreadyExistsException extends RestControllerAdviceException {
    public UserAlreadyExistsException(String error, String message) {
        super(error, message);
    }
}

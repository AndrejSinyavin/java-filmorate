package ru.yandex.practicum.filmorate.exceptions;

public final class UserValidationException extends RestControllerAdviceException {

    public UserValidationException(String error, String message) {
        super(error, message);
    }
}

package ru.yandex.practicum.filmorate.exceptions;

public final class UserServiceInternalException extends RestControllerAdviceException {

    public UserServiceInternalException(String error, String message) {
        super(error, message);
    }
}

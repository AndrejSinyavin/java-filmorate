package ru.yandex.practicum.filmorate.exceptions;

public final class UserNotFoundException extends RestControllerAdviceException {

    public UserNotFoundException(String error, String message) {
        super(error, message);
    }
}

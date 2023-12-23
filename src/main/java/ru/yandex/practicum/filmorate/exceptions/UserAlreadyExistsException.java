package ru.yandex.practicum.filmorate.exceptions;

public class UserAlreadyExistsException extends RestControllerAdviceException {

    public UserAlreadyExistsException(String error, String message) {
        super(error, message);
    }
}

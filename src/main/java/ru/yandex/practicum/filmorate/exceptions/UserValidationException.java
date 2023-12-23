package ru.yandex.practicum.filmorate.exceptions;

public class UserValidationException extends RestControllerAdviceException {

    public UserValidationException(String error, String message) {
        super(error, message);
    }
}

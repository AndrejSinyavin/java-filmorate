package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends RestControllerAdviceException {

    public UserNotFoundException(String error, String message) {
        super(error, message);
    }
}

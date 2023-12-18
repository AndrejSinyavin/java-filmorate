package ru.yandex.practicum.filmorate.exceptions;

public class UserAlreadyExistsException extends CustomException {
    public UserAlreadyExistsException(String error, String message) {
        super(error, message);
    }
}

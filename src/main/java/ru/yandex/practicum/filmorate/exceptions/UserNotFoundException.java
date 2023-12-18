package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends CustomException {

    public UserNotFoundException(String error, String message) {
        super(error, message);
    }
}

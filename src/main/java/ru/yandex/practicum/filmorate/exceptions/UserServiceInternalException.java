package ru.yandex.practicum.filmorate.exceptions;

public class UserServiceInternalException extends CustomException {

    public UserServiceInternalException(String error, String message) {
        super(error, message);
    }
}

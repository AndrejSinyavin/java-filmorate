package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException{
    public String message;

    public UserNotFoundException(String message) {
        super(message);
    }
}

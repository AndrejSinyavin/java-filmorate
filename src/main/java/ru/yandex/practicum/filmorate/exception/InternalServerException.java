package ru.yandex.practicum.filmorate.exception;

public class InternalServerException extends AppException {
    public InternalServerException(String source, String error, String message) {
        super(source, error, message);
    }
}
package ru.yandex.practicum.filmorate.exception;

public class InternalServiceException extends AppException {

    public InternalServiceException(String source, String error, String message) {
        super(source, error, message);
    }
}
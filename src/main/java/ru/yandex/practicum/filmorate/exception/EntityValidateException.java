package ru.yandex.practicum.filmorate.exception;

public class EntityValidateException extends AppException {

    public EntityValidateException(String source, String error, String message) {
        super(source, error, message);
    }
}
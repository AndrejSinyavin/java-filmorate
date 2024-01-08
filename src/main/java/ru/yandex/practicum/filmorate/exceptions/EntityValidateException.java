package ru.yandex.practicum.filmorate.exceptions;

public class EntityValidateException extends AppException {

    public EntityValidateException(String source, String error, String message) {
        super(source, error, message);
    }
}
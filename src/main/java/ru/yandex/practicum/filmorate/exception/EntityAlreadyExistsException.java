package ru.yandex.practicum.filmorate.exception;

public class EntityAlreadyExistsException extends AppException {

    public EntityAlreadyExistsException(String source, String error, String message) {
        super(source, error, message);
    }
}

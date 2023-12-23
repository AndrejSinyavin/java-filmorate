package ru.yandex.practicum.filmorate.exceptions;

public class FilmServiceInternalException extends RestControllerAdviceException {

    public FilmServiceInternalException(String error, String message) {
        super(error, message);
    }
}
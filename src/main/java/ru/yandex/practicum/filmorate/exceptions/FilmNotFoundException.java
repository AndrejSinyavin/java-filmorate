package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException extends RestControllerAdviceException {

    public FilmNotFoundException(String error, String message) {
        super(error, message);
    }
}

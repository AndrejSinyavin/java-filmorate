package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.CustomException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserServiceInternalException;

@RestControllerAdvice(value = "ru.yandex.practicum.filmorate.controllers")
public class AppRestControllerAdvice {
    @ExceptionHandler({UserAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUncorrectParametrs(final CustomException e) {
        return new ErrorResponse(e.getError(), e.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundError(final CustomException e) {
        return new ErrorResponse(e.getError(), e.getMessage());
    }

    @ExceptionHandler({UserServiceInternalException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerInternalError(final UserServiceInternalException e) {
        return new ErrorResponse(e.getError(), e.getMessage());
    }
}

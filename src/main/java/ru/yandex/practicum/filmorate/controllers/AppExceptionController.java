package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controllers")
public final class AppExceptionController {
    @ExceptionHandler({UserAlreadyExistsException.class, UserValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final RestControllerAdviceException e) {
        return new ErrorResponse(e.getError(), e.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundError(final RestControllerAdviceException e) {
        return new ErrorResponse(e.getError(), e.getMessage());
    }

    @ExceptionHandler({UserServiceInternalException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerInternalError(final RestControllerAdviceException e) {
        return new ErrorResponse(e.getError(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAnnotationValidateError(final MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("Валидация запроса в контроллере", "Обнаружены некорректные параметры");
        e.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return new ErrorResponse(errors);
    }
}

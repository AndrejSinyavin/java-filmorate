package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.*;

import javax.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Централизованный обработчик исключений приложения.
 */
@Slf4j
@RestControllerAdvice("ru.yandex.practicum.filmorate.controllers")
public final class AppErrorResponseController {

    /**
     * Обработчик исключений для ответов BAD_REQUEST.
     *
     * @param e перехваченное исключение
     * @return стандартный ответ об ошибке ErrorResponse
     */
    @ExceptionHandler({EntityAlreadyExistsException.class, EntityValidateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestResponse(final AppException e) {
        String message = "Некорректный запрос. Сформирован ответ '400 Bad Request'.";
        log.warn("{} {} {} {} \n{}",message, e.getSource(), e.getError(), e.getMessage(), e.getStackTrace());
        return new ErrorResponse(e.getError(), e.getMessage());
    }

    /**
     * Обработчик исключений для ответов NOT_FOUND.
     *
     * @param e перехваченное исключение
     * @return стандартный ответ об ошибке ErrorResponse
     */
    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundErrorResponse(final AppException e) {
        String message = "Не найден объект, необходимый для выполнения запроса. Сформирован ответ '404 Not found'.";
        log.warn("{} {} {} {} \n{}", message, e.getSource(), e.getError(), e.getMessage(), e.getStackTrace());
        return new ErrorResponse(e.getError(), e.getMessage());
    }

    /**
     * Обработчик исключений для ответов INTERNAL_SERVER_ERROR
     *
     * @param e перехваченное исключение
     * @return стандартный ответ об ошибке ErrorResponse
     */
    @ExceptionHandler({InternalServiceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerInternalErrorResponse(final AppException e) {
        String message = "Сервер не смог обработать запрос. Сформирован ответ '500 Internal Server Error'";
        log.warn("{} {} {} {} \n{}", message, e.getSource(), e.getError(), e.getMessage(), e.getStackTrace());
        return new ErrorResponse(e.getError(), e.getMessage());
    }

    /**
     * Обработчик исключений для ответов BAD_REQUEST при валидации в контроллере
     *
     * @param e перехваченное исключение
     * @return стандартный ответ об ошибке ErrorResponse
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAnnotationValidateErrorResponse(final MethodArgumentNotValidException e) {
        String message = "Некорректный запрос. Сформирован ответ '400 Bad Request'.";
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("Валидация запроса в контроллере", "Обнаружены некорректные параметры");
        e.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        log.warn("{} {}\n{}", message, errors, e.getStackTrace());
        return new ErrorResponse(errors);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidMetodParameterErrorResponse(final ConstraintViolationException e) {
        String message = "Некорректный параметр или переменная пути в запросе.";
        log.warn("{} Сформирован ответ '404 Not Found.' {}\n{}", message, e.getLocalizedMessage(), e.getStackTrace());
        return new ErrorResponse(message, e.getLocalizedMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerFailureResponse(final Throwable e) {
        String message = "Сбой в работе сервера.";
        log.warn("{} Сформирован ответ '500 Internal Server Error.' {}\n{}",
                message, e.getMessage(), e.getStackTrace());
        return new ErrorResponse(message, e.getMessage());
    }
}

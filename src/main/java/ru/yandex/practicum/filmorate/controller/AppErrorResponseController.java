package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.entity.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Централизованный обработчик исключений приложения для REST-full API.
 */
@Slf4j
@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
public class AppErrorResponseController {

    /**
     * Обработчик исключений для ответов BAD_REQUEST.
     *
     * @param e перехваченное исключение
     * @return стандартный API-ответ об ошибке ErrorResponse c указанием компонента, источника и вероятных причинах
     */
    @ExceptionHandler({EntityAlreadyExistsException.class, EntityValidateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestResponse(final AppException e) {
        String message = "Некорректный запрос. Сформирован ответ '400 Bad Request'.";
        log.warn("{} {} {} {} \n{}", message, e.getSource(), e.getError(), e.getMessage(), e.getStackTrace());
        return new ErrorResponse(e.getError(), e.getMessage());
    }

    /**
     * Обработчик исключений для ответов BAD_REQUEST для запросов с несоответствующим форматом тела или заголовков.
     *
     * @param e перехваченное исключение
     * @return стандартный API-ответ об ошибке ErrorResponse c указанием компонента, источника и вероятных причинах
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableExceptionResponse(final HttpMessageNotReadableException e) {
        String message = "Отсутствует тело запроса. Сформирован ответ '400 Bad Request'.";
        log.warn("{} {} {} {} \n{}", message, e.getHttpInputMessage(), e.getCause(), e.getMessage(), e.getStackTrace());
        return new ErrorResponse(message, e.getMessage());
    }

    /**
     * Обработчик исключений для ответов NOT_FOUND.
     *
     * @param e перехваченное исключение
     * @return стандартный API-ответ об ошибке ErrorResponse c указанием компонента, источника и вероятных причинах
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
     * @return стандартный API-ответ об ошибке ErrorResponse c указанием компонента, источника и вероятных причинах
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
     * @return стандартный API-ответ об ошибке ErrorResponse c указанием компонента, источника и вероятных причинах
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAnnotationValidateErrorResponse(final MethodArgumentNotValidException e) {
        String message = "Некорректный запрос. Сформирован ответ '400 Bad Request'.";
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("Валидация запроса в контроллере", "Обнаружены некорректные параметры в запросе");
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

    /**
     * Обработчик исключений для ответов NOT_FOUND при обработке параметров и/или переменных пути в запросах
     *
     * @param e перехваченное исключение
     * @return стандартный API-ответ об ошибке ErrorResponse c указанием компонента, источника и вероятных причинах
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidMetodParameterErrorResponse(final ConstraintViolationException e) {
        String message = "Некорректный параметр или переменная пути в запросе.";
        log.warn("{} Сформирован ответ '404 Not Found.' {}\n{}", message, e.getLocalizedMessage(), e.getStackTrace());
        return new ErrorResponse(message, e.getLocalizedMessage());
    }

    /**
     * Обработчик исключений для ответов INTERNAL_SERVER_ERROR при ошибках в работе СУБД
     *
     * @param e перехваченное исключение
     * @return стандартный API-ответ об ошибке ErrorResponse c указанием компонента, источника и вероятных причинах
     */
    @ExceptionHandler({DataAccessException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalDatabaseFailureErrorResponse(final ConstraintViolationException e) {
        String message = "Сбой в работе СУБД.";
        log.warn("{} Сформирован ответ '500 Internal Server Error.' {}\n{}",
                message, e.getLocalizedMessage(), e.getStackTrace());
        return new ErrorResponse(message, e.getLocalizedMessage());
    }

    /**
     * Обработчик исключений - заглушка, для обработки прочих непредусмотренных исключений.
     *
     * @param e перехваченное исключение
     * @return стандартный API-ответ об ошибке ErrorResponse c указанием компонента, источника и вероятных причинах
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerFailureResponse(final Throwable e) {
        String message = "Сбой в работе сервера.";
        log.warn("{} Сформирован ответ '500 Internal Server Error.'\n{}",
                message, e.getStackTrace());
        return new ErrorResponse(message, e.toString());
    }
}

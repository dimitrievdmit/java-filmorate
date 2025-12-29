package ru.yandex.practicum.filmorate.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.NoSuchElementException;

@SuppressWarnings("unused")
@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    /*
     Класс для логирования исключений
     и для возвращения более полных текстов ошибок
     и более подходящих кодов ответов.
    */

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(String.format("Validation error: %s", e.getClass()), e.getMessage());
        log.warn("{}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler({NoSuchElementException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(String.format("Not Found: %s", e.getClass()), e.getMessage());
        log.warn("{}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(String.format("Server error: %s", e.getClass()), e.getMessage());
        log.warn("{}", errorResponse);
        return errorResponse;
    }
}
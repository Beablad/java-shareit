package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandle {
    @ExceptionHandler
    @ResponseStatus (HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException (ConflictException e) {
        return Map.of("error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus (HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException (NotFoundException e) {
        return Map.of("error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus (HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException (ValidationException e) {
        return Map.of("error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus (HttpStatus.FORBIDDEN)
    public Map<String, String> handleNoAccessException (NoAccessException e) {
        return Map.of("error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage());
    }
}

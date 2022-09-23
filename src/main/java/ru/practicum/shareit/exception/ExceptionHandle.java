package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHandle {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(ConflictException e) {
        log.error(e.getMessage());
        return Map.of("error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage());
        return Map.of("error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException e) {
        log.error(e.getMessage());
        return Map.of("error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleNoAccessException(NoAccessException e) {
        log.error(e.getMessage());
        return Map.of("error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Throwable e) {
        log.error(e.getMessage());
        return Map.of("error", "Передан неверный идентификатор",
                "errorMessage", e.getMessage());
    }
}

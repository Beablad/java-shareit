package ru.practicum.shareit.exceptionTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorResponseTest {
    private final ErrorResponse errorResponse = new ErrorResponse("error");

    @Test
    public void getError() {
        assertEquals("error", errorResponse.getError());
    }
}
package ru.practicum.shareit.exception;

public class NoAccessException extends IllegalArgumentException {
    public NoAccessException(String s) {
        super(s);
    }
}

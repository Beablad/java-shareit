package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/booking")
@Slf4j
@Validated
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Время окончания не может быть больше времени начала");
        }
        log.info("Creating booking {}, userId={}", bookingRequestDto, userId);
        return bookingClient.createBooking(userId, bookingRequestDto);
    }

    @GetMapping("/bookingId")
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
                                             @RequestHeader(name = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfUser(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader(name = "X-Sharer-User-Id", defaultValue = "0") long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "20") @Positive int size) {
        return bookingClient.getBookingsOfUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                     @RequestHeader(name = "X-Sharer-User-Id", defaultValue = "0") long userId,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                     @RequestParam(defaultValue = "20") @Positive int size) {
        return bookingClient.getBookingsByOwner(state, userId, from, size);
    }
}

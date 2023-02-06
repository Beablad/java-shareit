package ru.practicum.shareit.booking.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDtoShort bookingDto,
                                    @RequestHeader(name = "X-Sharer-User-Id", defaultValue = "0") long ownerId) {
        return bookingService.createBooking(bookingDto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable long bookingId, @Param("approved") Boolean approved,
                                     @RequestHeader(name = "X-Sharer-User-Id", defaultValue = "0") long ownerId) {
        return bookingService.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader(name = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfUser(@RequestParam(defaultValue = "ALL") String state,
                                              @RequestHeader(name = "X-Sharer-User-Id", defaultValue = "0") long userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "20") int size) {
        return bookingService.getBookingsOfUser(Objects.requireNonNullElse(state, "ALL"), userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        return bookingService.getAllBookingByOwner(userId, state, from, size);
    }
}

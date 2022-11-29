package ru.practicum.shareit.booking.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.Objects;

/**
 * TODO Sprint add-bookings.
 */
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
    public List<BookingDto> getBookingsOfUser(@Param("state") String state,
                                              @RequestHeader(name = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        return bookingService.getBookingsOfUser(Objects.requireNonNullElse(state, "ALL"), userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingByOwner(userId, state);
    }
}

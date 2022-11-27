package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDtoShort bookingDtoShort, long userId);

    BookingDto approveBooking(long bookingId, Boolean approve, long userId);

    BookingDto getBooking(long bookingId, long userId);

    List<BookingDto> getBookingsOfUser(String state, long userId);

    List<BookingDto> getAllBookingByOwner(long userId, String state);
}

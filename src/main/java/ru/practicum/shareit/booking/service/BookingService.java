package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.List;

public interface BookingService {
    public BookingDto createBooking(BookingDtoShort bookingDtoShort, long userId);

    BookingDto approveBooking(long bookingId, Boolean approve, long userId);

    public BookingDto getBooking(long bookingId, long userId);

    public List<BookingDto> getBookingsOfUser(String state, long userId);

    public List<BookingDto> getAllBookingByOwner(long userId, String state);
}

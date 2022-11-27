package ru.practicum.shareit.booking.controller.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public class BookingMapper {

    public static Booking toBooking (BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto toBookingDto (Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking fromShortToBooking (BookingDtoShort bookingDtoShort) {
        return Booking.builder()
                .id(bookingDtoShort.getId())
                .start(bookingDtoShort.getStart())
                .end(bookingDtoShort.getEnd())
                .item(null)
                .booker(null)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDtoForItem toBookingDtoItem (Booking booking) {
        return BookingDtoForItem.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}

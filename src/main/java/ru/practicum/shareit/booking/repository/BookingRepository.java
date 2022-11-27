package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc (long userId);

    List<Booking> searchBookingByItemOwnerId(long userId);

    List<Booking> searchBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc (long itemId, LocalDateTime time);

    List<Booking> findBookingsByItemIdAndEndIsBeforeOrderByEndDesc (long itemId, LocalDateTime time);

    List<Booking> findBookingsByItemIdAndStartIsAfterOrderByStartDesc (long itemId, LocalDateTime time);

    List<Booking> findBookingsByItem_IdAndBookerId (long itemId, long userId);
}

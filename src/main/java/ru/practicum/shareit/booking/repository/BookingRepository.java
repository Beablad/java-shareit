package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> searchBookingByItemOwnerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime time);

    List<Booking> findBookingsByItemIdAndStartIsAfterOrderByStartDesc(long itemId, LocalDateTime time);

    List<Booking> findBookingsByItemIdAndBookerId(long itemId, long userId);
}

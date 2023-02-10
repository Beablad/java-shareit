package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
public class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private Item item;
    private User owner;
    private User booker;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        owner = userRepository.save(new User(1L, "testUser1", "test@yandex.ru"));
        booker = userRepository.save(new User(2L, "testUser2", "test1@yandex.ru"));
        item = itemRepository.save(new Item(1L, "testItem", "testDescription", true, owner,
                null));
        booking = bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(2), booker, item, BookingStatus.APPROVED));
    }

    @Test
    void findByBookerIdOrderByStartDesc() {
        final List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId(), Pageable.unpaged());

        assertEquals(booking.getId(), bookings.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookings.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookings.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookings.get(0).getStatus(), "Статусы не совпадают");
    }

    @Test
    void getBookerAndLocalDateTimeTest() {
        List<Booking> bookings = bookingRepository.findBookingsByItemIdAndStartIsAfterOrderByStartDesc(booker.getId(),
                LocalDateTime.now());

        assertEquals(0, bookings.size(), "Бронь есть");
    }

    @Test
    void searchBookingByItemOwnerId() {
        final List<Booking> bookings = bookingRepository.searchBookingByItemOwnerIdOrderByStartDesc(owner.getId(), Pageable.unpaged());

        assertEquals(1, bookings.size(), "Бронь отсутствует");
        assertEquals(booking.getId(), bookings.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookings.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookings.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookings.get(0).getStatus(), "Статусы не совпадают");
    }

    @Test
    void findBookingsByItemIdAndEndIsBeforeOrderByEndDesc() {
        final List<Booking> bookings = bookingRepository
                .findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());

        assertEquals(1, bookings.size(), "Бронь отсутствует");
        assertEquals(booking.getId(), bookings.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookings.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookings.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookings.get(0).getStatus(), "Статусы не совпадают");
    }

    @Test
    void getBookingsItemIdAndEndIsBeforeOrderByEndDescTest() {
        final List<Booking> bookings = bookingRepository
                .findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());

        assertEquals(1, bookings.size(), "Бронь отсутствует");
        assertEquals(booking.getId(), bookings.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookings.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookings.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookings.get(0).getStatus(), "Статусы не совпадают");
    }

    @Test
    void findBookingsByItemIdAndStartIsAfterOrderByStartDesc() {
        final Booking booking1 = bookingRepository.save(new Booking(2L, LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10), booker, item, BookingStatus.APPROVED));

        final List<Booking> bookings = bookingRepository.findBookingsByItemIdAndStartIsAfterOrderByStartDesc(item.getId(),
                LocalDateTime.now());

        assertEquals(1, bookings.size(), "Бронь отсутствует");
        assertEquals(booking1.getId(), bookings.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking1.getStart(), bookings.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking1.getEnd(), bookings.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking1.getBooker().getName(), bookings.get(0).getBooker().getName(), "Имена не совпадают");
        assertEquals(booking1.getStatus(), bookings.get(0).getStatus(), "Статусы не совпадают");
    }

    @Test
    void findBookingsByItemIdAndStartIsAfterOrderByStartDesc1() {
        List<Booking> bookings = bookingRepository
                .findBookingsByItemIdAndStartIsAfterOrderByStartDesc(item.getId(), LocalDateTime.now());

        assertEquals(0, bookings.size(), "Бронь есть");
    }
}
package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private Booking booking;
    private User booker;

    @BeforeEach
    private void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        booking = createTestBooking();
    }

    private Booking createTestBooking() {
        User owner = new User(1L, "testOwner", "testOwner@yandex.ru");
        booker = new User(2L, "testBooker", "testBooker@yandex.ru");
        Item item = new Item(1L, "testItem", "testDescription", true, owner, null);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        booking = new Booking(1L, start, end, booker, item, APPROVED);
        return booking;
    }

    private static BookingDtoShort toBookingDtoShort(Booking booking) {
        return new BookingDtoShort(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }

    @Test
    public void createValidBooking() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        Long itemId = booking.getItem().getId();
        Item item = booking.getItem();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto bookingDto = bookingService.createBooking(toBookingDtoShort(booking), bookerId);

        assertEquals(booking.getId(), bookingDto.getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDto.getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void getBooking() {
        Long bookerId = booking.getBooker().getId();
        Long bookingId = booking.getId();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBooking(bookingId, bookerId);

        assertEquals(booking.getId(), bookingDto.getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDto.getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    public void getBookingsOfUser() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        List<Booking> list = List.of(booking);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerIdOrderByStartDesc(booking.getBooker().getId(), PageRequest.of(0, 20)))
                .thenReturn(list);

        final List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(bookerId, "ALL", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDtoList.get(0).getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void getBookingsOfUserCurrent() {
        Long bookerId = booker.getId();
        booking.setStart(LocalDateTime.now());
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        final List<BookingDto> bookingDtoList = bookingService.getBookingsOfUser("CURRENT", bookerId, 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDtoList.get(0).getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void getBookingsOfUserFuture() {
        Long bookerId = booker.getId();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        final List<BookingDto> bookingDtoList = bookingService.getBookingsOfUser("FUTURE", bookerId, 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDtoList.get(0).getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(anyLong(),
                any());
    }

    @Test
    public void getBookingsOfUserWaiting() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();

        booking.setStatus(WAITING);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getBookingsOfUser("WAITING", bookerId, 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void getBookingsOfUserRejected() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();

        booking.setStatus(REJECTED);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getBookingsOfUser("REJECTED", bookerId, 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void getAllUserBookings() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();

        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "ALL", 0, 20);


        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

   /* @Test
    public void getAllUserBookingsCurrent() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();
        booking.setStart(LocalDateTime.now());
        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "CURRENT", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }*/

    @Test
    public void getAllUserBookingsPast() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "PAST", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void getAllUserBookingsFuture() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();
        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "FUTURE", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void getAllUserBookingsWaiting() {
        User booker = booking.getBooker();
        booking.setStatus(WAITING);
        Long itemUserId = booking.getItem().getOwner().getId();
        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "WAITING", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void getAllUserBookingsRejected() {
        User booker = booking.getBooker();
        booking.setStatus(REJECTED);
        Long itemUserId = booking.getItem().getOwner().getId();
        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "REJECTED", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).searchBookingByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void approveBookingNoOwnerItem() {
        Long bookingId = booking.getId();
        booking.setStatus(WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(bookingId, true, booker.getId()));

        assertEquals("Дождитесь подтвержения брони владельцем", throwable.getMessage(),
                "Текст ошибки валидации разный");

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    public void approveBookingApproved() {
        Long bookingId = booking.getId();
        Long itemUserId = booking.getItem().getOwner().getId();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(ValidationException.class, () -> bookingService.approveBooking(bookingId,
                true, itemUserId));
        assertNotNull(throwable.getMessage());

        assertEquals("Бронирование уже подтверждено", throwable.getMessage(),
                "Текст ошибки валидации разный");

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    public void approveBookingApproveNull() {
        Long bookingId = booking.getId();
        Long itemUserId = booking.getItem().getOwner().getId();

        booking.setStatus(WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(ValidationException.class, () -> bookingService.approveBooking(bookingId,
                null, itemUserId));
        assertNotNull(throwable.getMessage());

        assertEquals("Не указан статус бронирования", throwable.getMessage(),
                "Текст ошибки валидации разный");

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    public void createBookingUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("Неверный идентификатор пользователя"));

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(toBookingDtoShort(booking), 3L));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Неверный идентификатор пользователя");

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void createBookingFalseItem() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        Long itemId = booking.getItem().getId();
        Item item = booking.getItem();
        item.setAvailable(false);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Throwable throwable = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(toBookingDtoShort(booking), bookerId));

        assertEquals("Эта вещь недоступна для аренды", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    @Test
    public void createBookingOwnItem() {
        Item item = booking.getItem();
        Long ownerId = item.getOwner().getId();
        User owner = item.getOwner();
        Long itemId = booking.getItem().getId();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(toBookingDtoShort(booking), ownerId));

        assertEquals("Владелец вещи не может забронировать свою вещь", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    @Test
    public void getUnknownBooking() {
        Long bookingId = booking.getId();

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(bookingId, 3L));

        assertEquals("Бронь не найдена", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    @Test
    public void getAllBookingsUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingsOfUser("ALL", 3L, 0, 20));

        assertEquals("Пользователь не найден", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    @Test
    public void createBookingWithInvalidLocalDateTimeEnd() {
        LocalDateTime endError = booking.getEnd().minusDays(20);
        booking.setEnd(endError);

        Throwable throwable = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(toBookingDtoShort(booking), booking.getBooker().getId()));
        assertEquals("Время окончания не может быть больше времени начала", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    @Test
    public void getAllBookingUnsupportedStatus() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        Throwable throwable = assertThrows(IllegalArgumentException.class, () ->
                bookingService.getBookingsOfUser("APPROVED", bookerId, 0, 20));

        assertEquals("Unknown state: APPROVED", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    @Test
    public void getAllUserBookingUnsupportedStatus() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        Throwable throwable = assertThrows(IllegalArgumentException.class, () ->
                bookingService.getAllBookingByOwner(bookerId, "APPROVED", 0, 20));

        assertEquals("У пользователя нет вещей", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }
}

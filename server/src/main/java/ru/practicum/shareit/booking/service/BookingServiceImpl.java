package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingDtoShort bookingDtoShort, long userId) {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart())) {
            throw new ValidationException("Время окончания не может быть больше времени начала");
        }

        if (bookingDtoShort.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время начала брони не может быть в прошлом");
        }

        Booking booking = BookingMapper.fromShortToBooking(bookingDtoShort);

        booking.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден")));
        Optional<Item> itemOptional = itemRepository.findById(bookingDtoShort.getItemId());
        Item item = itemRepository.findById(bookingDtoShort.getItemId())
                .orElseThrow(() -> new NotFoundException("Неверный идентификатор вещи"));

        if (!item.getAvailable()) {
            throw new ValidationException("Эта вещь недоступна для аренды");
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец вещи не может забронировать свою вещь");
        }

        booking.setItem(item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(long bookingId, Boolean approve, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        if (booking.getBooker().getId() == userId) {
            throw new NotFoundException("Дождитесь подтвержения брони владельцем");
        } else if (booking.getItem().getOwner().getId() != userId) {
            throw new ValidationException("Только владелец может подтвердить бронирование");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approve == null) {
            throw new ValidationException("Не указан статус бронирования");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Нет доступа к просмотру брони");
        }
    }

    @Override
    public List<BookingDto> getBookingsOfUser(String state, long userId, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<BookingDto> bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        switch (BookingState.valueOf(state)) {
            case ALL:
                return bookingList;
            case CURRENT:
                return bookingList.stream().filter(booking -> LocalDateTime.now().isAfter(booking.getStart())
                        && LocalDateTime.now().isBefore(booking.getEnd())).collect(Collectors.toList());
            case PAST:
                return bookingList.stream().filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingList.stream().filter(booking -> LocalDateTime.now().isBefore(booking.getStart()))
                        .collect(Collectors.toList());
            case WAITING:
                return bookingList.stream().filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingList.stream().filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDto> getAllBookingByOwner(long userId, String state, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Неверный идентификатор пользователя"));

        List<BookingDto> bookingList = bookingRepository.searchBookingByItemOwnerIdOrderByStartDesc(userId, pageable).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        if (bookingList.isEmpty()) {
            throw new NotFoundException("У пользователя нет вещей");
        }
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookingList.sort(Comparator.comparing(BookingDto::getStart).reversed());
                return bookingList;
            case CURRENT:
                return bookingList.stream().filter(booking -> LocalDateTime.now().isAfter(booking.getStart())
                        && LocalDateTime.now().isBefore(booking.getEnd())).collect(Collectors.toList());
            case PAST:
                return bookingList.stream().filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingList.stream().filter(booking -> LocalDateTime.now().isBefore(booking.getStart()))
                        .collect(Collectors.toList());
            case WAITING:
                return bookingList.stream().filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingList.stream().filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }
}

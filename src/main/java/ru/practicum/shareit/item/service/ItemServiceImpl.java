package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class
ItemServiceImpl implements ItemService {

    @NotNull
    ItemRepository itemRepository;
    @NotNull
    UserRepository userRepository;
    @NotNull
    BookingRepository bookingRepository;

    @NotNull
    CommentRepository commentRepository;

    @NotNull ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        if (userId == 0) {
            throw new ValidationException("");
        }
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            itemRequestRepository.findById(itemDto.getRequestId()).ifPresent(item::setRequest);
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ValidationException("Идентификатор вещи не указан или вещи не существует"));
        if (ownerId == 0) {
            throw new ValidationException("Не указан идетификатор");
        }
        if (item.getOwner().getId() != ownerId) {
            throw new NoAccessException("Нет доступа к изменению вещи");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDtoBooking getItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор вещи"));

        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);

        if (item.getOwner().getId().equals(userId)) {
            createItemDtoWithBooking(itemDtoBooking);
        }

        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        if (!comments.isEmpty()) {
            itemDtoBooking.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        }
        return itemDtoBooking;
    }

    public List<ItemDtoBooking> getItemsOfUser(long userId, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<ItemDtoBooking> itemDtoBookings = itemRepository.findByOwnerId(userId, pageable).stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDtoBooking)
                .sorted(Comparator.comparing(ItemDtoBooking::getId))
                .collect(Collectors.toList());
        for (ItemDtoBooking itemDtoBooking : itemDtoBookings) {
            createItemDtoWithBooking(itemDtoBooking);
            List<Comment> comments = commentRepository.findAllByItem_Id(itemDtoBooking.getId());
            if (!comments.isEmpty()) {
                itemDtoBooking.setComments(comments.stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()));
            }
        }
        return itemDtoBookings;
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        if (!text.isBlank()) {
            return itemRepository.search(text, pageable)
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<ItemDtoBooking> getItems() {
        return itemRepository.findAll().stream().map(ItemMapper::toItemDtoBooking).collect(Collectors.toList());
    }

    private void createItemDtoWithBooking(ItemDtoBooking itemDtoBooking) {
        List<Booking> lastBookings = bookingRepository
                .findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(itemDtoBooking.getId(),
                        LocalDateTime.now());

        if (!lastBookings.isEmpty()) {
            BookingDtoForItem lastBooking = BookingMapper.toBookingDtoItem(lastBookings.get(0));
            itemDtoBooking.setLastBooking(lastBooking);
        }

        List<Booking> nextBookings = bookingRepository
                .findBookingsByItemIdAndStartIsAfterOrderByStartDesc(itemDtoBooking.getId(),
                        LocalDateTime.now());

        if (!nextBookings.isEmpty()) {
            BookingDtoForItem nextBooking = BookingMapper.toBookingDtoItem(nextBookings.get(0));
            itemDtoBooking.setNextBooking(nextBooking);
        }
    }

    public CommentDto createComment(long itemId, long userId, CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Напишите текст комментария");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Comment comment = CommentMapper.toComment(commentDto);
        List<Booking> bookingList = bookingRepository.findBookingsByItemIdAndBookerId(itemId, userId)
                .stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
        if (bookingList.isEmpty()) {
            throw new ValidationException("Вы не бронировали данную вещь или бронь еще не завершилась");
        } else {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(item);
            comment.setUser(user);
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        }
    }
}

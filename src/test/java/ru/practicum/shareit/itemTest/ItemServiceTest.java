package ru.practicum.shareit.itemTest;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceTest {

    ItemService itemService;
    ItemRepository itemRepository;
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;
    Item item;
    User user;

    @BeforeEach
    private void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository);
        item = createTestItem();
    }

    private Item createTestItem() {
        user = User.builder()
                .name("test user")
                .email("test@test.ru")
                .id(1L)
                .build();
        ItemRequest itemRequest = createRequest();
        return Item.builder()
                .id(1L)
                .name("test item")
                .description("test description")
                .owner(user)
                .available(true)
                .request(itemRequest)
                .build();
    }

    private ItemRequest createRequest() {
        return ItemRequest.builder()
                .id(1L)
                .requestor(createSecondUser())
                .description("test request")
                .created(LocalDateTime.now())
                .build();
    }

    private User createSecondUser() {
        return User.builder()
                .name("test1 user")
                .email("test1@test.ru")
                .id(2L)
                .build();
    }

    @Test
    void createItem() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);
        itemService.createItem(itemDto, user.getId());

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());

        verify(itemRepository, times(1)).save(item);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void updateItem() {
        Item item1 = createTestItem();
        item1.setName("test update");

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.updateItem(ItemMapper.toItemDto(item1), item.getOwner().getId(), item.getId());

        assertEquals(item.getName(), itemDto.getName());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ItemDtoBooking itemDto = itemService.getItemById(item.getId(), user.getId());

        assertEquals(item.getId(), itemDto.getId());

        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void getItemsOfUser() {
        List<Item> items = List.of(item);
        when(itemRepository.findByOwnerId(user.getId(), PageRequest.of(0,20))).thenReturn(items);
        List<ItemDtoBooking> listDtos = itemService.getItemsOfUser(user.getId(), 0, 20);

        assertEquals(items.get(0).getId(), listDtos.get(0).getId());

        verify(itemRepository, times(1)).findByOwnerId(user.getId(), PageRequest.of(0,20));
    }

    @Test
    void searchItem() {
        String searchText = "test";
        List<Item> list = List.of(item);
        Pageable pageable = PageRequest.of(0, 20);
        when(itemRepository.search(searchText, pageable)).thenReturn(list);
        List<ItemDto> dtoList = itemService.searchItem(searchText, 0, 20);

        assertEquals(list.get(0).getId(), dtoList.get(0).getId());
        assertEquals(list.get(0).getName(), dtoList.get(0).getName());

        verify(itemRepository, times(1)).search(searchText, pageable);
    }

    @Test
    void getItems() {
        List<Item> items = List.of(item);
        when(itemRepository.findAll()).thenReturn(items);
        List<ItemDtoBooking> listDtos = itemService.getItems();

        assertEquals(items.get(0).getId(), listDtos.get(0).getId());

        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void createComment() {
        User user2 = item.getRequest().getRequestor();
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(user2)
                .start(LocalDateTime.of(2000, 1, 1, 0, 0, 0))
                .end(LocalDateTime.of(2000, 1, 2, 0, 0, 0))
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("test comment")
                .item(item)
                .user(user2)
                .created(LocalDateTime.now())
                .build();
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);

        when(bookingRepository
                .findBookingsByItemIdAndBookerId(anyLong(), anyLong()))
                .thenReturn(bookingsList);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDto1 = CommentMapper.toCommentDto(comment);
        CommentDto commentDto = itemService.createComment(user2.getId(), item.getId(), commentDto1);

        assertEquals("test comment", commentDto.getText());
        assertEquals(user2.getName(), commentDto.getAuthorName());
        assertEquals(comment.getId(), commentDto.getId());

        verify(commentRepository, times(1)).save(any());
    }
}
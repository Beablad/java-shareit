package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    private ItemRequestService itemRequestService;
    private ItemRequestRepository itemRequestRepository;
    private ItemRequestMapper itemRequestMapper;
    private UserRepository userRepository;
    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    public void beforeEach() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        ItemRepository itemRepository = mock(ItemRepository.class);
        itemRequestMapper = new ItemRequestMapper();
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        itemRequest = createItemRequestExample();
    }

    private ItemRequest createItemRequestExample() {
        user = new User(1L, "test", "test@yandex.ru");
        return new ItemRequest(1L, "itemRequestDescription", user, LocalDateTime.now());
    }

    //Создание запроса
    @Test
    public void createValidItemRequest() {
        Long itemRequestId = itemRequest.getId();
        Long userId = itemRequest.getRequestor().getId();
        User user = itemRequest.getRequestor();

        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(ItemRequestMapper.toItemRequestDto(itemRequest),
                userId);

        assertEquals(itemRequestId, itemRequestDto.getId(), "Идентификаторы не совпадают");
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription(), "Описания не совпадают");
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated(), "Время не совпадает");

        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    public void getItemRequest() {
        Long itemRequestId = itemRequest.getId();
        Long userId = itemRequest.getRequestor().getId();
        User user = itemRequest.getRequestor();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestDtoWithItems itemRequestDtoWithItems = itemRequestService.getRequestById(userId, itemRequestId);
    }

    @Test
    public void getAllItemRequests() {
        Long itemRequestId = itemRequest.getId();
        Long userId = itemRequest.getRequestor().getId();
        User user = itemRequest.getRequestor();
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest),
                PageRequest.of(0, 20), 20);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        itemRequestService.createItemRequest(ItemRequestMapper.toItemRequestDto(itemRequest), userId);

        final List<ItemRequestDtoWithItems> itemRequestDtoWithItems = itemRequestService
                .getAllItemRequest(0, 20, userId);
    }

    @Test
    public void createItemRequestUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemRequestService.createItemRequest(itemRequestMapper.toItemRequestDto(itemRequest), 3L));

        assertEquals("Пользователь не найден", throwable.getMessage(),
                "Неверный идентификатор пользователя");
    }

    @Test
    public void getItemRequestUnknownUser() {
        Long itemRequestId = itemRequest.getId();

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(3L, itemRequestId));

        assertEquals("ПОльзователь не найден", throwable.getMessage(),
                "Попробуйте другой идентификатор");
    }

    @Test
    public void getItemRequestUnknownRequestId() {
        Long userId = itemRequest.getRequestor().getId();

        when(userRepository.findById(anyLong())).thenReturn((Optional.of(user)));

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(userId, 100L));

        assertEquals("Запрос не найден", throwable.getMessage(),
                "Попробуйте другой идентификатор");
    }

    @Test
    public void getAllItemRequestsUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemRequestService.getAllItemRequest(0, 20, 3L));

        assertEquals("Пользователь не найден", throwable.getMessage(),
                "Неверный идентификатор пользователя");
    }

    @Test
    public void getAllItemRequestWithPageableUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemRequestService.getAllItemRequest(0, 20, 3L));

        assertEquals("Пользователь не найден", throwable.getMessage(),
                "Неверный идентификатор пользователя");
    }
}

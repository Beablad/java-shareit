package ru.practicum.shareit.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
public class RequestRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        User user = userRepository.save(new User(1L, "test", "test@yandex.ru"));
        itemRequest = itemRequestRepository.save(new ItemRequest(1L, "itemRequest1", user,
                LocalDateTime.now()));
    }

    @Test
    void getAllByRequestorIdOrderByCreatedDesc() {
        final List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorId(itemRequest.getRequestor().getId());

        assertEquals(itemRequests.size(), 1, "Запрос отсутствует");
        assertEquals(itemRequest.getId(), itemRequests.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(itemRequest.getDescription(), itemRequests.get(0).getDescription(),
                "Описания не совпадают");
        assertEquals(itemRequest.getCreated(), itemRequests.get(0).getCreated(),
                "Время не совпадает");
    }
}
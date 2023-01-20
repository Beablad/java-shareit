package ru.practicum.shareit.itemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemControllerTest {
    @Autowired
    MockMvc mvc;
    final ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    ItemService itemService;

    Item item;

    @BeforeEach
    public void beforeEach() {
        objectMapper.registerModule(new JavaTimeModule());
        item = createTestItem();
    }

    private Item createTestItem() {
        User user1 = User.builder().id(1L).name("test1").email("test1@test.ru").build();
        User user2 = User.builder().id(2L).name("test2").email("test2@test.ru").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("test")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build();
        return Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .owner(user1)
                .request(itemRequest)
                .build();
    }

    private CommentDto createTestComment() {
        return CommentDto.builder()
                .id(1L)
                .text("Test Comment")
                .authorName(item.getOwner().getName())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    public void createItem() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.createItem(itemDto, item.getOwner().getId())).thenReturn(itemDto);
        mvc.perform(post("/items").content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", item.getOwner().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"Test Item\"," +
                        " \"description\": \"Test description\", \"available\": true, \"requestId\": 1}"));
        verify(itemService, times(1)).createItem(itemDto, item.getOwner().getId());
    }

    @Test
    public void getItemById() throws Exception {
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        itemService.createItem(ItemMapper.toItemDto(item), 1);
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoBooking);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", item.getOwner().getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1, \"name\": \"Test Item\", " +
                        "\"description\": \"Test description\", \"lastBooking\": null, \"nextBooking\": null," +
                        "\"comments\": []}"));
        verify(itemService, times(1)).getItemById(item.getId(), item.getOwner().getId());
    }

    @Test
    public void getItems() throws Exception {
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        List<ItemDtoBooking> list = List.of(itemDtoBooking);
        when(itemService.getItems()).thenReturn(list);
        mvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1, \"name\": \"Test Item\", " +
                        "\"description\": \"Test description\", \"lastBooking\": null, \"nextBooking\": null," +
                        "\"comments\": []}]"));
        verify(itemService, times(1)).getItems();
    }

    @Test
    public void searchItem() throws Exception {
        String text = "Test";
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<ItemDto> list = List.of(itemDto);
        when(itemService.searchItem(text, 0, 20)).thenReturn(list);
        mvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("[{\"id\": 1,\"name\": \"Test Item\"," +
                        " \"description\": \"Test description\", \"available\": true, \"requestId\": 1}]"));
        verify(itemService, times(1)).searchItem(text, 0, 20);
    }

    @Test
    public void createComment() throws Exception {
        CommentDto commentDto = createTestComment();
        LocalDateTime created = commentDto.getCreated();
        when(itemService.createComment(item.getId(), item.getOwner().getId(), commentDto)).thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", item.getOwner().getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":1, \"text\": \"Test Comment\", \"authorName\": \"test1\"}"));
        verify(itemService, times(1)).createComment(anyLong(), anyLong(), any());
    }
}

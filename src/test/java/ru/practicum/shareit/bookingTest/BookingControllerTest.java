package ru.practicum.shareit.bookingTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingControllerTest {

    MockMvc mvc;
    final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    BookingService bookingService;
    @InjectMocks
    BookingController bookingController;
    private Booking booking;

    @BeforeEach
    public void beforeEach() {
        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper.registerModule(new JavaTimeModule());
        booking = createTestBooking();
    }

    private Booking createTestBooking() {
        User user1 = User.builder().id(1L).name("test1").email("test1@test,ru").build();
        User user2 = User.builder().id(1L).name("test2").email("test2@test,ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(true)
                .owner(user1)
                .request(null)
                .build();
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 2, 1, 0, 0, 0))
                .end(LocalDateTime.of(2023, 2, 2, 0, 0, 0))
                .booker(user2)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    private BookingDtoShort toShort(Booking booking) {
        return new BookingDtoShort(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }

    @Test
    public void createBooking() throws Exception {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(toShort(booking))))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1, \"status\":\"WAITING\", \"item\": " +
                        "{\"id\": 1, \"name\": \"test\", \"description\": \"test\"}}"))
                .andDo(print());
        verify(bookingService, times(1)).createBooking(any(), anyLong());
    }

    @Test
    public void getBooking() throws Exception {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1, \"status\":\"WAITING\", \"item\": " +
                        "{\"id\": 1, \"name\": \"test\", \"description\": \"test\"}}"));
        verify(bookingService, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    public void getBookingOfUser() throws Exception {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        List<BookingDto> list = List.of(bookingDto);
        when(bookingService.getBookingsOfUser(any(), anyLong(), anyInt(), anyInt())).thenReturn(list);
        mvc.perform(get("/bookings")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1, \"status\":\"WAITING\", \"item\": " +
                        "{\"id\": 1, \"name\": \"test\", \"description\": \"test\"}}]"));
        verify(bookingService, times(1)).getBookingsOfUser(any(), anyLong(),anyInt(), anyInt());
    }

    @Test
    public void getAllBookingByOwner() throws Exception {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        List<BookingDto> list = List.of(bookingDto);
        when(bookingService.getAllBookingByOwner(1, "WAITING", 0, 10)).thenReturn(list);
        mvc.perform(get("/bookings/owner")
                .param("state", "WAITING")
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(bookingService, times(1)).getAllBookingByOwner(anyLong(), any(), any(), any());
    }
}

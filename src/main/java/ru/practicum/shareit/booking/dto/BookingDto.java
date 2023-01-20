package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    long id;
    @NotNull(message = "Время начала бронирования не может быть пустым")
    LocalDateTime start;
    @NotNull (message = "Время окончания бронирования не может быть пустым")
    LocalDateTime end;
    @NotNull
    Item item;
    @NotNull
    User booker;
    BookingStatus status;
}

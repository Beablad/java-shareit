package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.controller.dto.BookingDtoForItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
public class ItemDtoBooking {
    Long id;
    @NotBlank(message = "Имя вещи не может быть пустым")
    String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    String description;
    @NotNull(message = "Статус не может быть null")
    Boolean available;
    BookingDtoForItem lastBooking;
    BookingDtoForItem nextBooking;
    List<CommentDto> comments;
}
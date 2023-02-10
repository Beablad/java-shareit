package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults (level = AccessLevel.PRIVATE)
@Builder
public class ItemRequestDtoWithItems {

    long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items;
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item, long id);

    ItemDto updateItem(ItemDto itemDto, long id, long itemId);

    ItemDtoBooking getItemById(long itemId, long userId);

    List<ItemDtoBooking> getItemsOfUser(long ownerId, Integer from, Integer size);

    List<ItemDto> searchItem(String text, Integer from, Integer size);

    List<ItemDtoBooking> getItems();

    CommentDto createComment(long itemId, long userId, CommentDto commentDto);
}

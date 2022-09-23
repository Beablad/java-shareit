package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item, int id);

    ItemDto updateItem(ItemDto itemDto, int id, int itemId);

    ItemDto getItemById(int id);

    List<ItemDto> getItemsOfUser(int ownerId);

    List<ItemDto> searchItem(String text);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public ItemDto createItem(ItemDto item, int id);

    public ItemDto updateItem(ItemDto itemDto, int id, int itemId);

    public ItemDto getItemById(int id);

    public List<ItemDto> getItemsOfUser(int ownerId);

    public List<ItemDto> searchItem(String text);
}

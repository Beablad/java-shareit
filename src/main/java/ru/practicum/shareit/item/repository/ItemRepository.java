package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {

    Item createItem(Item item, User owner);

    Item updateItem(Item item, int itemId);

    Item getItemById(int id);

    List<ItemDto> getItemsOfUser(int ownerId);

    List<Item> searchItem(String text);
}

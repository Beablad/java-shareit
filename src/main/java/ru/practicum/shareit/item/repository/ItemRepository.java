package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {

    public Item createItem(Item item, User owner);

    public Item updateItem(Item item, int itemId);

    public Item getItemById(int id);

    public List<ItemDto> getItemsOfUser(int ownerId);

    public List<Item> searchItem(String text);
}

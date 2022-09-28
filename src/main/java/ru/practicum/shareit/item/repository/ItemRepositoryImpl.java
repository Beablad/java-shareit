package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRepositoryImpl implements ItemRepository {

    Map<Integer, Item> items;

    public Item createItem(Item item, User owner) {
        item.setOwner(owner);
        items.put(item.getId(), item);
        if (item.getAvailable() == null || item.getName() == null || item.getDescription() == null) {
            throw new ValidationException("Неверные данные при создании предмета");
        }
        return item;
    }

    public Item updateItem(Item item, int itemId) {
        Item changeableItem = items.get(itemId);
        if (item.getName() != null) {
            changeableItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            changeableItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            changeableItem.setAvailable(item.getAvailable());
        }
        return changeableItem;
    }

    public Item getItemById(int id) {
        return items.get(id);
    }

    public List<ItemDto> getItemsOfUser(int ownerId) {
        List<ItemDto> itemsOfUser = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == ownerId) {
                itemsOfUser.add(ItemMapper.toItemDto(item));
            }
        }
        return itemsOfUser;
    }

    public List<Item> searchItem(String text) {
        final String searchText = text.toLowerCase();
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Predicate<Item> inName = item -> item.getName().toLowerCase().contains(searchText);
        Predicate<Item> inDesc = item -> item.getDescription().toLowerCase().contains(searchText);
        return new ArrayList<>(items.values())
                .stream()
                .filter(inName.or(inDesc))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}

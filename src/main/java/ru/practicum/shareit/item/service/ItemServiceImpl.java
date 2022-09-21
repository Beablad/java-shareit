package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {

    @NotNull
    final ItemRepository itemRepository;
    @NotNull
    final UserRepository userRepository;
    int id = 1;


    public ItemDto createItem(ItemDto item, int id) {
        if (id == 0) {
            throw new ValidationException("Неверный идентификатор");
        }
        User user = userRepository.getUserById(id).orElseThrow(() -> new NotFoundException("Неверный идентификатор"));
        item.setId(getNextId());
        return ItemMapper.toItemDto(itemRepository.createItem(ItemMapper.toItem(item), user));
    }

    public ItemDto updateItem(ItemDto itemDto, int ownerId, int itemId) {
         Item changeableItem = itemRepository.getItemById(itemId);
        if (changeableItem.getOwner().getId() == ownerId) {
            return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItem(itemDto), itemId));
        } else if (ownerId == 0) {
            throw new ValidationException("Не указан идентификатор пользователя");
        } else {
            throw new NoAccessException("Нет доступа к изменению предмета");
        }
    }

    public ItemDto getItemById(int id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }

    public List<ItemDto> getItemsOfUser (int ownerId) {
        return itemRepository.getItemsOfUser(ownerId);
    }

    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItem(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private int getNextId() {
        return id++;
    }
}

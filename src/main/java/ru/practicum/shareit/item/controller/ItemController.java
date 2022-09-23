package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {

    ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid() @RequestBody ItemDto item,
                              @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") int ownerId) {
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto item,
                              @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") int ownerId,
                              @PathVariable int itemId) {
        return itemService.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsOfUser(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") int ownerId) {
        return itemService.getItemsOfUser(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(required = false) String text) {
        return itemService.searchItem(text);
    }
}

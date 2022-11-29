package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
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
                              @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long ownerId) {
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto item,
                              @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long ownerId,
                              @PathVariable long itemId) {
        return itemService.updateItem(item, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking getItemById(@PathVariable long itemId,
                                      @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoBooking> getItems(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long ownerId) {
        if (ownerId == 0) {
            return itemService.getItems();
        } else {
            return itemService.getItemsOfUser(ownerId);
        }
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(required = false) String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId, @RequestBody CommentDto commentDto,
                                    @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        return itemService.createComment(itemId, userId, commentDto);
    }
}

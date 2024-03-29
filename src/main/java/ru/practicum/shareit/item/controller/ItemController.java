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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

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
    public List<ItemDtoBooking> getItems(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long ownerId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam (defaultValue = "10") @Positive Integer size) {
        return itemService.getItemsOfUser(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam (defaultValue = "10") @Positive Integer size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId, @RequestBody CommentDto commentDto,
                                    @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        return itemService.createComment(itemId, userId, commentDto);
    }
}

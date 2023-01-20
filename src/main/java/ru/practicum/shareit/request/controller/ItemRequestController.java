package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                            @RequestHeader("X-Sharer-User-Id") long requestorId) {
        return itemRequestService.createItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getItemRequestWithAnswers
            (@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long requestorId) {
        return itemRequestService.getItemRequestWithAnswers(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllItemRequest(@RequestParam(value = "from", defaultValue = "0") int from,
                                                           @RequestParam(value = "size", defaultValue = "20") int size,
                                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllItemRequest(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getRequestById(@RequestHeader("X-Sharer-User-Id") long userId
            , @PathVariable long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}

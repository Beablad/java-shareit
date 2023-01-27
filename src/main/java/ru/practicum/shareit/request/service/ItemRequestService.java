package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long requestorId);

    List<ItemRequestDtoWithItems> getItemRequestWithAnswers(long requestorId);

    List<ItemRequestDtoWithItems> getAllItemRequest(int from, int size, long userId);

    ItemRequestDtoWithItems getRequestById(long userId, long requestId);
}

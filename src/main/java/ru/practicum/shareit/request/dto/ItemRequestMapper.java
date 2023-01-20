package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto (ItemRequest itemRequest){
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest (ItemRequestDto itemRequestDto){
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(itemRequestDto.getRequestor())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestDtoWithItems toItemRequestDtoWithItems (ItemRequest itemRequest) {
        return ItemRequestDtoWithItems.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }
}

package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long requestorId) {
        User user = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoWithItems> getItemRequestWithAnswers(long requestorId) {
        User user = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequestDtoWithItems> itemRequestList =
                getListOfItemRequestDtoWithItems(itemRequestRepository.findAllByRequestorId(requestorId));

        return itemRequestList;
    }

    public List<ItemRequestDtoWithItems> getAllItemRequest(int from, int size, long userId) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Указаны не верные параметры запроса");
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAll(pageable).stream()
                .filter(itemRequest -> !Objects.equals(itemRequest.getRequestor().getId(), userId))
                .collect(Collectors.toList());
//        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(userId);
        return getListOfItemRequestDtoWithItems(itemRequestList);
    }

    public ItemRequestDtoWithItems getRequestById(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("ПОльзователь не найден"));
        List<ItemRequest> itemRequest = List.of(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден")));
        return getListOfItemRequestDtoWithItems(itemRequest).get(0);
    }

    private List<ItemRequestDtoWithItems> getListOfItemRequestDtoWithItems(List<ItemRequest> list) {
        List<ItemRequestDtoWithItems> itemRequestList =
                list.stream().map(ItemRequestMapper::toItemRequestDtoWithItems).collect(Collectors.toList());
        for (ItemRequestDtoWithItems itemRequest : itemRequestList) {
            List<ItemDto> itemDtoList = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                    .map(ItemMapper::toItemDto).collect(Collectors.toList());
            if (!itemDtoList.isEmpty()) {
                itemRequest.setItems(itemDtoList);
            }
        }
        return itemRequestList;
    }
}

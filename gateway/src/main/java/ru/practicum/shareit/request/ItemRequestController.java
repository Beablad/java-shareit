package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@AllArgsConstructor
@RequestMapping("/requests")
@Validated
public class ItemRequestController {

    final private ItemRequestClient itemRequestClient;
    final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                                    @RequestHeader(userIdHeader) long requestorId) {
        return itemRequestClient.createItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestWithAnswer(@RequestHeader(userIdHeader) long requestorId) {
        return itemRequestClient.getItemRequestWithAnswer(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader(userIdHeader) long requestorId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "20") @Positive int size) {
        return itemRequestClient.getAllItemRequest(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId,
                                                 @RequestHeader(userIdHeader) long requestorId) {
        return itemRequestClient.getItemRequestById(requestorId, requestId);
    }
}

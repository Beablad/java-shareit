package ru.practicum.shareit.booking.controller.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
public class BookingDtoForItem {
    long id;
    long bookerId;
}

package ru.practicum.shareit.request.dto;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    long id;
    @NotNull
    String description;
    User requestor;
    LocalDateTime created;
}

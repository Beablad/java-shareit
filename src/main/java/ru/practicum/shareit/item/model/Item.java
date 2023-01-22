package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Column(name = "item_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "item_name")
    @NotBlank
    String name;
    @Column
    @NotBlank
    String description;
    @Column
    @NotNull
    Boolean available;
    @OneToOne
    @JoinColumn(name = "user_id")
    User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}

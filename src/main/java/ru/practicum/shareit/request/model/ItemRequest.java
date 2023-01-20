package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemRequest {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "request_id")
    @EqualsAndHashCode.Include
    long id;
    @Column
    String description;
    @JoinColumn (name = "requestor_id")
    @ManyToOne
    User requestor;
    @Column
    LocalDateTime created;
}

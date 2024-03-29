package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "request_id")
    long id;
    @Column
    String description;
    @JoinColumn (name = "requestor_id")
    @ManyToOne
    User requestor;
    @Column
    LocalDateTime created;
}

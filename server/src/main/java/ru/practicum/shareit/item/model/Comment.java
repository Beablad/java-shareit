package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    long id;
    @Column
    String text;
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;
    @OneToOne
    @JoinColumn(name = "author_id")
    User user;
    @Column
    LocalDateTime created;
}

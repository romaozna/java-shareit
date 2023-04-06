package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String description;
    @JoinColumn(name = "requester_id", nullable = false)
    private Long requesterId;
    @Column(name = "created")
    @CreationTimestamp
    private LocalDateTime created;
    @OneToMany
    @JoinColumn(name = "request_id")
    private List<Item> items;
}

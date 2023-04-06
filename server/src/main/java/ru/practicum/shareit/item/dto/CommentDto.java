package ru.practicum.shareit.item.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentDto {
        private Long id;
        private String text;
        private String authorName;
        @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
        private LocalDateTime created;
}

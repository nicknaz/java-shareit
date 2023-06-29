package ru.practicum.server.item.dto;

import lombok.*;
import ru.practicum.server.item.model.Item;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    private String text;

    private Item item;

    private String authorName;

    private LocalDateTime created;

}

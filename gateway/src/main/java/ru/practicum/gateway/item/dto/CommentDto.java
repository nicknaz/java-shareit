package ru.practicum.gateway.item.dto;

import lombok.*;
import ru.practicum.gateway.item.model.Item;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotBlank
    private String text;

    private Item item;

    private String authorName;

    private LocalDateTime created;

}

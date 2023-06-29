package ru.practicum.gateway.item.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    private Long id;

    @NotNull
    private String text;

    private Item item;

    private String authorName;

    private LocalDateTime created;

}

package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private LocalDate created;
    private User requestor;
}

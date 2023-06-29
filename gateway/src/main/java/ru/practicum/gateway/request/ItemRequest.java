package ru.practicum.gateway.request;

import lombok.*;
import ru.practicum.gateway.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    private Long id;

    private String description;

    private LocalDateTime createdDate;

    private User requestor;
}

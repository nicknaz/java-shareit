package ru.practicum.shareit.item.dto;

import lombok.*;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoForRequest {
    private long id;

    private String name;

    private Long ownerId;

    private String description;

    private Boolean available;

    private Long requestId;
}

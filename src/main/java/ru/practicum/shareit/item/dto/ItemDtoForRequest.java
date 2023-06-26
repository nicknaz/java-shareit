package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoForRequest {
    @NotNull
    @PositiveOrZero
    private long id;
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private Long ownerId;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;

    private Long requestId;
}

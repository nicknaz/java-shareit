package ru.practicum.gateway.item.model;

import lombok.*;
import ru.practicum.gateway.request.ItemRequest;
import ru.practicum.gateway.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @NotNull
    @PositiveOrZero
    private long id;

    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    @NotNull
    private User owner;

    private ItemRequest request;

}

package ru.practicum.gateway.item.dto;

import lombok.*;
import ru.practicum.gateway.booking.dto.BookingDtoForItem;
import ru.practicum.gateway.item.model.Comment;
import ru.practicum.gateway.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoWithDates {
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
    private ItemRequest request;

    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;

    private List<Comment> comments;
}

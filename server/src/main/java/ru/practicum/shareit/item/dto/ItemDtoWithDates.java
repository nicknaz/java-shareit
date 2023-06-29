package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoWithDates {

    private long id;

    private String name;

    private String description;

    private Boolean available;
    private ItemRequest request;

    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;

    private List<Comment> comments;
}

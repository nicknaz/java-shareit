package ru.practicum.server.item.dto;

import lombok.*;
import ru.practicum.server.booking.dto.BookingDtoForItem;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.request.ItemRequest;

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

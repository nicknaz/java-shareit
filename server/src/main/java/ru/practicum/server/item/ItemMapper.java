package ru.practicum.server.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.dto.BookingDtoForItem;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemDtoForRequest;
import ru.practicum.server.item.dto.ItemDtoWithDates;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.user.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemDtoForRequest toItemDtoForReques(Item item) {
        return ItemDtoForRequest
                .builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto dto, User user, ItemRequest request) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(user)
                .request(request)
                .build();
    }

    public static ItemDtoWithDates toItemDtoWithDates(Item item,
                                                      BookingDtoForItem last,
                                                      BookingDtoForItem next,
                                                      List<Comment> comments) {
        return ItemDtoWithDates
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments)
                .build();
    }
}

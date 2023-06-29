package ru.practicum.gateway.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.booking.dto.BookingDtoForItem;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.item.dto.ItemDtoForRequest;
import ru.practicum.gateway.item.dto.ItemDtoWithDates;
import ru.practicum.gateway.item.model.Comment;
import ru.practicum.gateway.item.model.Item;
import ru.practicum.gateway.request.ItemRequest;
import ru.practicum.gateway.user.model.User;

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

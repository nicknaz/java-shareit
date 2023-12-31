package ru.practicum.gateway.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.item.dto.ItemDtoForRequest;
import ru.practicum.gateway.request.dto.ItemRequestDto;
import ru.practicum.gateway.request.dto.ItemRequestDtoForResponse;
import ru.practicum.gateway.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto dto, User user) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .createdDate(LocalDateTime.now())
                .requestor(user)
                .build();
    }

    public static ItemRequestDtoForResponse toItemRequestResponseDto(ItemRequest itemRequest,
                                                                     List<ItemDtoForRequest> items) {
        return ItemRequestDtoForResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreatedDate())
                .items(items)
                .build();
    }
}

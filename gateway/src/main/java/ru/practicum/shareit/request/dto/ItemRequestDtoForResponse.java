package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ItemRequestDtoForResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoForRequest> items = new ArrayList<>();
}

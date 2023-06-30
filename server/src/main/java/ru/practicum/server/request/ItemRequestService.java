package ru.practicum.server.request;

import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestDtoForResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoForResponse create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDtoForResponse> getForUser(Long userId);

    List<ItemRequestDtoForResponse>  getOtherUsers(Long userId, Integer from, Integer size);

    ItemRequestDtoForResponse getRequestById(Long userId, Long requestId);
}

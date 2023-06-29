package ru.practicum.server.item;

import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemDtoWithDates;
import ru.practicum.server.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto item, Long ownerId);

    List<ItemDtoWithDates> getAllByOwner(Long ownerId);

    ItemDtoWithDates getById(Long userId, Long itemId);

    ItemDto update(Long id, ItemDto item, Long ownerId);

    void delete(Long id);

    List<ItemDto> search(String text);

    Comment createComment(Long userId, Long itemId, CommentDto commentDto);
}

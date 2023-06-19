package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDates;
import ru.practicum.shareit.item.model.Comment;

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

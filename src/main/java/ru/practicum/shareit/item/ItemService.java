package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto item, Long ownerId);

    List<ItemDto> getAllByOwner(Long ownerId);

    ItemDto getById(Long id);

    ItemDto update(Long id, ItemDto item, Long ownerId);

    void delete(Long id);

    List<ItemDto> search(String text);
}

package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    Item add(Item item);

    List<Item> getAll();

    List<Item> getAllByOwner(User user);

    Item getById(Long id);

    Item update(Long id, Item item);

    void delete(Long id);
}

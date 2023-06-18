package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private long idGenerator = 1;
    private HashMap<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        item.setId(idGenerator);
        items.put(idGenerator++, item);
        return item;
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByOwner(User user) {
        return items.values().stream().filter(x -> x.getOwner() == user).collect(Collectors.toList());
    }

    @Override
    public Item getById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundedException("Вещь с таким id не найден");
        }
        return items.get(id);
    }

    @Override
    public Item update(Long id, Item item) {
        if (!items.containsKey(id)) {
            throw new NotFoundedException("Вещь с таким id не найден");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            items.get(id).setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            items.get(id).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            items.get(id).setAvailable(item.getAvailable());
        }
        return items.get(id);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }
}

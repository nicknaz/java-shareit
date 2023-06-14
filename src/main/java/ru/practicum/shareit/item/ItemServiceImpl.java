package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService{
    private ItemRepositoryImpl itemRepository;
    private UserRepositoryImpl userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryImpl itemRepository, UserRepositoryImpl userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto add(ItemDto item, Long ownerId) {
        return ItemMapper.toItemDto(itemRepository.add(ItemMapper.toItem(item, userRepository.getById(ownerId))));
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        return itemRepository.getAllByOwner(userRepository.getById(ownerId))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        return ItemMapper.toItemDto(itemRepository.getById(id));
    }

    @Override
    public ItemDto update(Long id, ItemDto item, Long ownerId) {
        if (itemRepository.getById(id).getOwner().getId() != ownerId) {
            throw new AccessException("Вы не можете изменить эту вещь, так как не вы её владелец!");
        }
        return ItemMapper.toItemDto(itemRepository
                .update(id, ItemMapper.toItem(item, userRepository.getById(ownerId))));
    }

    @Override
    public void delete(Long id) {
        itemRepository.delete(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.getAll()
                .stream()
                .filter(Item::getAvailable)
                .map(x -> Item.builder()
                        .id(x.getId())
                        .name(x.getName().toLowerCase())
                        .description(x.getDescription().toLowerCase())
                        .build())
                .filter(x -> !text.isBlank() && (x.getName().contains(text.toLowerCase())
                        || x.getDescription().contains(text.toLowerCase())))
                .map(x -> itemRepository.getById(x.getId()))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}

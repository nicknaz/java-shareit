package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundedException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.repository.ItemRepositoryJPA;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.repository.RequestRepositoryJPA;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private ItemRepositoryJPA itemRepository;
    private UserRepositoryJPA userRepository;
    private RequestRepositoryJPA requestRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRepositoryJPA itemRepository,
                           UserRepositoryJPA userRepository,
                           RequestRepositoryJPA requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemRequestDtoForResponse create(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundedException("Пользователь не найден"));
        return ItemRequestMapper.toItemRequestResponseDto(
                requestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user)),
                null);
    }

    @Override
    public List<ItemRequestDtoForResponse> getForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundedException("Пользователь не найден"));
        return requestRepository.findAllByRequestorOrderByCreatedDateDesc(user)
                .stream()
                .map(x -> (ItemRequestMapper.toItemRequestResponseDto(x, getListItem(x.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoForResponse> getOtherUsers(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundedException("Пользователь не найден"));
        return requestRepository.findAllByRequestorIsNotOrderByCreatedDateDesc(user, PageRequest.of(from / size, size))
                .stream()
                .map(x -> (ItemRequestMapper.toItemRequestResponseDto(x, getListItem(x.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoForResponse getRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundedException("Пользователь не найден"));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundedException("Запрос не найден"));

        return ItemRequestMapper.toItemRequestResponseDto(itemRequest, getListItem(requestId));
    }

    private List<ItemDtoForRequest> getListItem(Long requestId) {
        return itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(x -> (ItemMapper.toItemDtoForReques(x)))
                .collect(Collectors.toList());
    }
}

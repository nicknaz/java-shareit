package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundedException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJPA;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.repository.RequestRepositoryJPA;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ItemRepositoryJPA itemRepository;

    @Autowired
    private UserRepositoryJPA userRepository;

    @Autowired
    private RequestRepositoryJPA requestRepository;

    @Test
    public void testCreate() {
        User user = User.builder()
                .email("email@gmail.com")
                .name("Name")
                .build();
        userRepository.save(user);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Request 1 description");

        ItemRequestDtoForResponse result = itemRequestService.create(itemRequestDto, user.getId());

        assertNotNull(result.getId());
        assertEquals("Request 1 description", result.getDescription());
    }

    @Test
    public void testGetForUser() {
        User user = User.builder()
                .email("email@gmail.com")
                .name("Name")
                .build();
        userRepository.save(user);

        ItemRequest itemRequest1 = createItemRequest(user);
        ItemRequest itemRequest2 = createItemRequest(user);

        List<ItemRequestDtoForResponse> result = itemRequestService.getForUser(user.getId());

        assertEquals(2, result.size());
        assertEquals(itemRequest1.getId(), result.get(1).getId());
        assertEquals(itemRequest2.getId(), result.get(0).getId());
    }

    @Test
    public void testGetOtherUsers() {
        User user = User.builder()
                .email("email@gmail.com")
                .name("Name")
                .build();
        user = userRepository.save(user);

        User user2 = userRepository.save(User.builder()
                .email("email2@gmail.com")
                .name("Name2")
                .build());

        ItemRequest itemRequest1 = createItemRequest(user);
        ItemRequest itemRequest2 = createItemRequest(user);
        ItemRequest itemRequest3 = createItemRequest(user2);

        List<ItemRequestDtoForResponse> result = itemRequestService.getOtherUsers(user.getId(), 0, 2);

        assertEquals(1, result.size());
        assertEquals(itemRequest3.getId(), result.get(0).getId());
    }

    @Test
    public void testGetRequestById() {
        User user = User.builder()
                .email("email@gmail.com")
                .name("Name")
                .build();
        userRepository.save(user);

        ItemRequest itemRequest = createItemRequest(user);

        ItemRequestDtoForResponse result = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getRequestor(), user);
    }

    @Test
    public void testGetListItem(){
        User owner = new User();
        owner.setEmail("email@gmail.com");
        owner.setName("name" );
        owner = userRepository.save(owner);

        User user = new User();
        user.setEmail("email2@gmail.com");
        user.setName("name");
        user = userRepository.save(user);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setOwner(owner);
        ItemRequest request = createItemRequest(user);
        item.setRequest(request);
        item = itemRepository.save(item);

        List<ItemDtoForRequest> items = itemRepository.findAllByRequestId(request.getId())
                .stream()
                .map(x -> (ItemMapper.toItemDtoForReques(x)))
                .collect(Collectors.toList());

        assertEquals(items.size(), 1);

        ItemDtoForRequest itemDtoForRequest = items.get(0);
        assertEquals(itemDtoForRequest.getId(), 1);
        assertEquals(itemDtoForRequest.getAvailable(), true);
        assertEquals(itemDtoForRequest.getOwnerId(), owner.getId());
    }

    @Test
    public void testGetRequestByIdNotFound() {
        assertThrows(NotFoundedException.class, () -> itemRequestService.getRequestById(1L, 1L));
    }

    private ItemRequest createItemRequest(User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request description");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreatedDate(LocalDateTime.now());
        return requestRepository.save(itemRequest);
    }
}
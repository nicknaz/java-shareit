package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.repository.BookingRepositoryJPA;
import ru.practicum.shareit.exception.NotFoundedException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDates;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepositoryJPA;
import ru.practicum.shareit.item.repository.ItemRepositoryJPA;
import ru.practicum.shareit.request.repository.RequestRepositoryJPA;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ItemRepositoryJPA itemRepository;

    @Autowired
    private BookingRepositoryJPA bookingRepository;

    @Autowired
    private UserRepositoryJPA userRepository;

    @Autowired
    private CommentRepositoryJPA commentRepository;

    @Autowired
    private RequestRepositoryJPA requestRepository;

    @Test
    public void testAdd() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Item 1 description");
        itemDto.setAvailable(true);

        User owner = new User();
        owner.setEmail("email_bob@gmail.com");
        owner.setName("Sponge Bob");
        owner = userRepository.save(owner);

        ItemDto result = itemService.add(itemDto, owner.getId());

        assertNotNull(result.getId());
        assertEquals("Item 1", result.getName());
        assertEquals("Item 1 description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    public void testGetAllByOwner() {
        User owner = createUser();
        User booker = createUser();

        Item item1 = createItem(owner);
        Item item2 = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);

        Booking booking = createBookingDtoRequest(item1, booker, start, end);

        List<ItemDtoWithDates> result = itemService.getAllByOwner(owner.getId());

        BookingDtoForItem bookingDtoForItem = result.get(0).getNextBooking();

        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item2.getId(), result.get(1).getId());

        assertEquals(bookingDtoForItem.getId(), 1);
        assertEquals(bookingDtoForItem.getStart(), start);
        assertEquals(bookingDtoForItem.getEnd(), end);
        assertEquals(bookingDtoForItem.getStatus(), BookingStatus.APPROVED);
        assertEquals(bookingDtoForItem.getBookerId(), booker.getId());
        assertEquals(bookingDtoForItem.getItem(), item1);
    }

    @Test
    public void testGetById() {
        User user = createUser();

        Item item = createItem(user);

        ItemDtoWithDates result = itemService.getById(user.getId(), item.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    public void testGetByIdNotFound() {
        Long userId = 1L;
        Long itemId = 1L;

        assertThrows(NotFoundedException.class, () -> itemService.getById(userId, itemId));
    }

    @Test
    public void testUpdate() {
        User owner = createUser();

        Item item = createItem(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Item description");
        itemDto.setAvailable(false);

        ItemDto result = itemService.update(item.getId(), itemDto, owner.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals("Updated Item", result.getName());
        assertEquals("Updated Item description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    public void testUpdateNotFound() {
        Long itemId = 1L;
        Long ownerId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Item description");
        itemDto.setAvailable(false);

        assertThrows(NotFoundedException.class, () -> itemService.update(itemId, itemDto, ownerId));
    }

    @Test
    public void testUpdateAccessDenied() {
        Long itemId = 1L;

        User owner = createUser();
        User other = createUser();

        createItem(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Item description");
        itemDto.setAvailable(false);

        assertThrows(RuntimeException.class, () -> itemService.update(itemId, itemDto, other.getId()));
    }

    @Test
    public void testDelete() {
        User owner = createUser();

        Item item = createItem(owner);

        itemService.delete(item.getId());

        Optional<Item> deletedItem = itemRepository.findById(item.getId());
        assertFalse(deletedItem.isPresent());
    }

    @Test
    public void testSearch() {
        User owner1 = createUser();
        User owner2 = createUser();
        User owner3 = createUser();


        Item item1 = createItem(owner1);
        Item item2 = createItem(owner2);
        Item item3 = createItem(owner3);

        item1.setName("Laptop");
        item1.setDescription("Powerful laptop for gaming");

        item2.setName("Phone");
        item2.setDescription("Smartphone with a great camera");

        item3.setName("Tablet");
        item3.setDescription("Portable tablet for productivity");

        itemRepository.saveAll(Arrays.asList(item1, item2, item3));

        List<ItemDto> result = itemService.search("laptop");

        assertEquals(1, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
    }

    @Test
    public void testCreateComment() {
        User owner = createUser();
        User user = createUser();

        Item item = createItem(owner);

        bookingService.create(
                BookingDtoRequest
                        .builder()
                        .itemId(item.getId())
                        .end(LocalDateTime.now().plusHours(2))
                        .start(LocalDateTime.now().minusMinutes(10))
                        .status(BookingStatus.WAITING)
                        .build(), user.getId());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comm");

        Comment result = itemService.createComment(user.getId(), item.getId(), commentDto);

        assertNotNull(result.getId());
        assertEquals("comm", result.getText());
        assertEquals(userRepository.findById(user.getId()).get().getName(), result.getAuthorName());
        assertEquals(item.getId(), result.getItem().getId());
    }

    private User createUser() {
        User owner = new User();
        owner.setEmail("email" + LocalDateTime.now().getNano() + "@gmail.com");
        owner.setName("name" + + LocalDateTime.now().getNano());
        return userRepository.save(owner);
    }

    private Item createItem(User owner) {
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private Booking createBookingDtoRequest(Item item, User booker, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.save(BookingMapper.toBooking(BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .build(), item, booker));
    }
}

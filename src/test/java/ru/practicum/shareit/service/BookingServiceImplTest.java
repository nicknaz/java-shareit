package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.repository.BookingRepositoryJPA;
import ru.practicum.shareit.exception.InvalidDateException;
import ru.practicum.shareit.exception.NotFoundedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJPA;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    @Autowired
    private BookingServiceImpl bookingService;
    @Autowired
    private BookingRepositoryJPA bookingRepository;
    @Autowired
    private UserRepositoryJPA userRepository;
    @Autowired
    private ItemRepositoryJPA itemRepository;

    @Test
    public void testCreateBooking() {
        User user = createUser();
        User booker = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        BookingDto bookingDto = bookingService.create(bookingDtoRequest, booker.getId());

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getBooker().getId(), booker.getId());
        assertEquals(bookingDto.getStart(), bookingDtoRequest.getStart());
        assertEquals(bookingDto.getEnd(), bookingDtoRequest.getEnd());
        assertEquals(bookingDto.getStatus(), bookingDtoRequest.getStatus());

    }

    @Test
    public void testThrowItemNotFounded() {
        User user = createUser();
        User booker = createUser();
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(Item.builder()
                .id(100)
                .name("")
                .description("")
                .available(true)
                .owner(user)
                .build(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        assertThrows(NotFoundedException.class, () -> bookingService.create(bookingDtoRequest, booker.getId()));
    }

    @Test
    public void testThrowUserNotFounded() {
        User user = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        assertThrows(NotFoundedException.class, () -> bookingService.create(bookingDtoRequest, 99L));
    }

    @Test
    public void testThrowConflictId() {
        User user = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        assertThrows(NotFoundedException.class, () -> bookingService.create(bookingDtoRequest, user.getId()));
    }

    @Test
    public void testThrowAvailable() {
        User user = createUser();
        User booker = createUser();
        Item item = createItem(user);
        item.setAvailable(false);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoRequest, booker.getId()));
    }

    @Test
    public void testThrowInvalidDate() {
        User user = createUser();
        User booker = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now().plusDays(1), LocalDateTime.now());
        assertThrows(InvalidDateException.class, () -> bookingService.create(bookingDtoRequest, booker.getId()));
    }

    @Test
    public void testChangeStatusToApproved() {
        User user = createUser();
        User booker = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        BookingDto bookingDto = bookingService.create(bookingDtoRequest, booker.getId());

        bookingService.changeStatus(user.getId(), bookingDto.getId(), true);

        assertEquals(bookingRepository.findById(bookingDto.getId()).get().getStatus(), BookingStatus.APPROVED);
    }

    @Test
    public void testChangeStatusToRejected() {
        User user = createUser();
        User booker = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        BookingDto bookingDto = bookingService.create(bookingDtoRequest, booker.getId());

        bookingService.changeStatus(user.getId(), bookingDto.getId(), false);

        assertEquals(bookingRepository.findById(bookingDto.getId()).get().getStatus(), BookingStatus.REJECTED);
    }

    @Test
    public void testFindById() {
        User user = createUser();
        User booker = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        BookingDto bookingDto = bookingService.create(bookingDtoRequest, booker.getId());

        assertEquals(bookingService.findById(user.getId(), bookingDto.getId()), bookingDto);
    }

    @Test
    public void testFindByIdThrowOtherUser() {
        User user = createUser();
        User otherUser = createUser();
        User booker = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        BookingDto bookingDto = bookingService.create(bookingDtoRequest, booker.getId());

        assertThrows(NotFoundedException.class, () -> bookingService.findById(otherUser.getId(), bookingDto.getId()));
    }

    @Test
    public void testFindByIdThrowUserNotFounded() {
        User user = createUser();
        User booker = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        BookingDto bookingDto = bookingService.create(bookingDtoRequest, booker.getId());

        assertThrows(NotFoundedException.class, () -> bookingService.findById(99L, bookingDto.getId()));
    }

    @Test
    public void testFindByIdThrowBookingNotFounded() {
        User user = createUser();
        User booker = createUser();
        Item item = createItem(user);
        BookingDtoRequest bookingDtoRequest = createBookingDtoRequest(item, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        BookingDto bookingDto = bookingService.create(bookingDtoRequest, booker.getId());

        assertThrows(NotFoundedException.class, () -> bookingService.findById(booker.getId(), 99L));
    }

    @Test
    public void testFindAllByBookerALL() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        assertEquals(bookingService.findAllByBooker(booker.getId(), State.ALL, 0, 10).size(), 3);
    }

    @Test
    public void testFindAllByBookerFuture() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        assertEquals(bookingService.findAllByBooker(booker.getId(), State.FUTURE, 0, 10).size(), 1);
        assertEquals(bookingService.findAllByBooker(booker.getId(), State.FUTURE, 0, 10).get(0), bookingDto1);
    }

    @Test
    public void testFindAllByBookerCurrent() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        assertEquals(bookingService.findAllByBooker(booker.getId(), State.CURRENT, 0, 10).size(), 1);
        assertEquals(bookingService.findAllByBooker(booker.getId(), State.CURRENT, 0, 10).get(0), bookingDto2);
    }

    @Test
    public void testFindAllByBookerPast() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        assertEquals(bookingService.findAllByBooker(booker.getId(), State.PAST, 0, 10).size(), 1);
        assertEquals(bookingService.findAllByBooker(booker.getId(), State.PAST, 0, 10).get(0), bookingDto3);
    }

    @Test
    public void testFindAllByBookerWaiting() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        bookingService.changeStatus(user.getId(), bookingDto2.getId(), false);

        assertEquals(bookingService.findAllByBooker(booker.getId(), State.WAITING, 0, 10).size(), 2);
    }

    @Test
    public void testFindAllByBookerRejected() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        bookingService.changeStatus(user.getId(), bookingDto2.getId(), false);

        assertEquals(bookingService.findAllByBooker(booker.getId(), State.REJECTED, 0, 10).size(), 1);
    }

    //for owner
    @Test
    public void testFindAllByOwnerALL() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        assertEquals(bookingService.findAllByOwner(user.getId(), State.ALL, 0, 10).size(), 3);
    }

    @Test
    public void testFindAllByOwnerFuture() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        assertEquals(bookingService.findAllByOwner(user.getId(), State.FUTURE, 0, 10).size(), 1);
        assertEquals(bookingService.findAllByOwner(user.getId(), State.FUTURE, 0, 10).get(0), bookingDto1);
    }

    @Test
    public void testFindAllByOwnerCurrent() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        assertEquals(bookingService.findAllByOwner(user.getId(), State.CURRENT, 0, 10).size(), 1);
        assertEquals(bookingService.findAllByOwner(user.getId(), State.CURRENT, 0, 10).get(0), bookingDto2);
    }

    @Test
    public void testFindAllByOwnerPast() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        assertEquals(bookingService.findAllByOwner(user.getId(), State.PAST, 0, 10).size(), 1);
        assertEquals(bookingService.findAllByOwner(user.getId(), State.PAST, 0, 10).get(0), bookingDto3);
    }

    @Test
    public void testFindAllByOwnerWaiting() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        bookingService.changeStatus(user.getId(), bookingDto2.getId(), false);

        assertEquals(bookingService.findAllByOwner(user.getId(), State.WAITING, 0, 10).size(), 2);
    }

    @Test
    public void testFindAllByOwnerRejected() {
        User user = createUser();
        User booker = createUser();
        Item item1 = createItem(user);
        Item item2 = createItem(user);
        Item item3 = createItem(user);
        BookingDtoRequest bookingDtoRequest1 = createBookingDtoRequest(item1, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest2 = createBookingDtoRequest(item2, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusDays(1));
        BookingDtoRequest bookingDtoRequest3 = createBookingDtoRequest(item3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        BookingDto bookingDto1 = bookingService.create(bookingDtoRequest1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(bookingDtoRequest2, booker.getId());
        BookingDto bookingDto3 = bookingService.create(bookingDtoRequest3, booker.getId());

        bookingService.changeStatus(user.getId(), bookingDto2.getId(), false);

        assertEquals(bookingService.findAllByOwner(user.getId(), State.REJECTED, 0, 10).size(), 1);
    }



    private User createUser() {
        User owner = new User();
        owner.setEmail("email" + LocalDateTime.now().getNano() + "@gmail.com");
        owner.setName("name" + + LocalDateTime.now().getNano());
        return userRepository.save(owner);
    }

    private Item createItem(User owner) {
        return itemRepository.save(Item.builder()
                .description("Description")
                .name("Title")
                .owner(owner)
                .available(true).build());
    }

    private BookingDtoRequest createBookingDtoRequest(Item item, LocalDateTime start, LocalDateTime end) {
        return BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();
    }

}
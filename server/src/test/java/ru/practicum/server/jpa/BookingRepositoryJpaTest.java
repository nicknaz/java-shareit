package ru.practicum.server.jpa;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.booking.repository.BookingRepositoryJPA;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepositoryJPA;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepositoryJPA;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class BookingRepositoryJpaTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepositoryJPA bookingRepositoryJPA;
    @Autowired
    private ItemRepositoryJPA itemRepositoryJPA;
    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    @Test
    void testItemSaved() throws Exception {
        LocalDateTime time = LocalDateTime.now();

        User user = new User(1L, "user1", "email1@gmail.com");
        User userInRep = userRepositoryJPA.save(user);

        Item item = new Item(1L, "item", "description", true, userInRep, null);
        Item itemInRep = itemRepositoryJPA.save(item);

        Booking booking = new Booking(1L, itemInRep, time.plusMinutes(1),
                time.plusDays(1), userInRep, BookingStatus.WAITING);
        Booking bookingInRep = bookingRepositoryJPA.save(booking);

        Booking retrievedBooking = entityManager.find(Booking.class, bookingInRep.getId());

        Assertions.assertThat(retrievedBooking).isEqualTo(bookingInRep);
    }

    @Test
    public void testFindAllByOwnerIdAndItemIdAndEndBeforeOrderByStartDesc() {
        User owner = User.builder().name("name1").email("email1@email.com").build();
        User booker = User.builder().name("name2").email("email2@email.com").build();

        owner = userRepositoryJPA.save(owner);
        booker = userRepositoryJPA.save(booker);

        Item item = Item.builder().available(true).description("vs").owner(owner).name("name").build();

        item = itemRepositoryJPA.save(item);

        Booking booking1 = Booking.builder()
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build();

        Booking booking2 = Booking.builder()
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build();

        Booking booking3 = Booking.builder()
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1)).build();


        booking1 = bookingRepositoryJPA.save(booking1);
        booking2 = bookingRepositoryJPA.save(booking2);
        booking3 = bookingRepositoryJPA.save(booking3);

        assertThat(bookingRepositoryJPA.findAllByOwnerIdAndItemIdAndEndBeforeOrderByStartDesc(owner.getId(),
                item.getId(), LocalDateTime.now())).isEqualTo(List.of(booking2, booking3));
    }
}

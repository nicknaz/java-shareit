package ru.practicum.server.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepositoryJPA extends JpaRepository<Booking, Long> {

    //for booker
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable page);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime date, Pageable page);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime date, Pageable page);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                             LocalDateTime date1,
                                                                             LocalDateTime date2,
                                                                             Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable page);

    List<Booking> findAllByBookerIdAndItemIdAndStatusNotAndStartBeforeOrderByStartDesc(Long bookerId,
                                                                                       Long itemId,
                                                                                       BookingStatus status,
                                                                                       LocalDateTime date,
                                                                                       Pageable page);

    //for owner
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime date, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime date, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                                LocalDateTime date1,
                                                                                LocalDateTime date2,
                                                                                Pageable page);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable page);

    //Здесь сложные условия, поэтому нельзя запросный метод использовать
    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 and i.id = ?2 " +
            "and (b.start < ?3 and b.end > ?3 or b.end < ?3) " +
            " order by b.start desc")
    List<Booking> findAllByOwnerIdAndItemIdAndEndBeforeOrderByStartDesc(Long ownerId, Long itemId, LocalDateTime date);

    List<Booking> findAllByItemOwnerIdAndItemIdAndStartAfterOrderByStartDesc(Long ownerId,
                                                                             Long itemId,
                                                                             LocalDateTime date);

}

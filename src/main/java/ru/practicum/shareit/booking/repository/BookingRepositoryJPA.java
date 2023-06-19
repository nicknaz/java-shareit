package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepositoryJPA extends JpaRepository<Booking, Long> {

    //for booker
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                         LocalDateTime date1,
                                                                                         LocalDateTime date2);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndItemIdAndStatusNotAndStartBeforeOrderByStartDesc(Long bookerId,
                                                                                       Long itemId,
                                                                                       BookingStatus status,
                                                                                       LocalDateTime date);

    //for owner
    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 and b.end < ?2" +
            " order by b.start desc")
    List<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime date);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 and b.start > ?2" +
            " order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime date);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 and b.start < ?2 and b.end > ?2" +
            " order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime date);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 and b.status = ?2" +
            " order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 and i.id = ?2 " +
            "and (b.start < ?3 and b.end > ?3 or b.end < ?3) " +
            " order by b.start desc")
    List<Booking> findAllByOwnerIdAndItemIdAndEndBeforeOrderByStartDesc(Long ownerId, Long itemId, LocalDateTime date);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 and i.id = ?2 and b.start > ?3" +
            " order by b.start desc")
    List<Booking> findAllByOwnerIdAndItemIdAndStartAfterOrderByStartDesc(Long ownerId, Long itemId, LocalDateTime date);

}

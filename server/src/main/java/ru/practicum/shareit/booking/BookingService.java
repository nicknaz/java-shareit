package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoRequest bookingDto, Long bookerId);

    BookingDto changeStatus(Long ownerId, Long bookingId, boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllByBooker(Long bookerId, State state, Integer from, Integer size);

    List<BookingDto> findAllByOwner(Long bookerId, State state, Integer from, Integer size);
}

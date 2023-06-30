package ru.practicum.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.item.model.Item;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class BookingDtoForItem {
    private Long id;

    private Item item;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long bookerId;

    private BookingStatus status;
}

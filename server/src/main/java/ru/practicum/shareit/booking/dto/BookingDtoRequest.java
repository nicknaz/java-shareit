package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDtoRequest {
    private Long id;

    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}

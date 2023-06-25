package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDtoForItem {
    @PositiveOrZero
    private Long id;

    @NotNull
    private Item item;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long bookerId;

    private BookingStatus status;
}

package ru.practicum.gateway.booking;

import lombok.*;
import ru.practicum.gateway.item.model.Item;
import ru.practicum.gateway.user.model.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @PositiveOrZero
    private Long id;

    @NotNull
    private Item item;

    private LocalDateTime start;

    private LocalDateTime end;

    private User booker;

    private BookingStatus status;
}

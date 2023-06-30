package ru.practicum.server.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingDtoForItem;
import ru.practicum.server.booking.dto.BookingDtoRequest;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto
                .builder()
                .id(booking.getId())
                .item(booking.getItem())
                .end(booking.getEnd())
                .start(booking.getStart())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }



    public static BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return BookingDtoForItem
                .builder()
                .id(booking.getId())
                .item(booking.getItem())
                .end(booking.getEnd())
                .start(booking.getStart())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }


    public static Booking toBooking(BookingDtoRequest dto, Item item, User user) {
        return Booking.builder()
                .id(dto.getId())
                .item(item)
                .end(dto.getEnd())
                .start(dto.getStart())
                .status(dto.getStatus())
                .booker(user)
                .build();
    }
}

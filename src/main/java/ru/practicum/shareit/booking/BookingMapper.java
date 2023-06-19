package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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

    public static BookingDto toBookingDto(BookingDtoRequest bookingDtoRequest, Item item) {
        return BookingDto
                .builder()
                .id(bookingDtoRequest.getId())
                .item(item)
                .end(bookingDtoRequest.getEnd())
                .start(bookingDtoRequest.getStart())
                .status(bookingDtoRequest.getStatus())
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

    public static Booking toBooking(BookingDto dto, User user) {
        return Booking.builder()
                .id(dto.getId())
                .item(dto.getItem())
                .end(dto.getEnd())
                .start(dto.getStart())
                .status(dto.getStatus())
                .booker(user)
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

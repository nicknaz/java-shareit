package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;

    @Autowired
    public  BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                             @Valid @RequestBody BookingDtoRequest bookingDto) {
        log.info("Create booking by bookerId={} with startTime={}", bookerId, bookingDto.getStart());
        BookingDto resultDto = bookingService.create(bookingDto, bookerId);
        return resultDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                  @PathVariable Long bookingId, @RequestParam boolean approved) {
        BookingDto resultDto = bookingService.changeStatus(ownerId, bookingId, approved);
        log.info("Change status booking by ownerId={}", ownerId);
        return resultDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        BookingDto resultDto = bookingService.findById(userId, bookingId);
        log.info("Get booking by bookingId={}", bookingId);
        return resultDto;
    }

    @GetMapping
    public List<BookingDto> findAllByBooker(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(name = "from", defaultValue = "0")
                                            @PositiveOrZero Integer from,
                                            @RequestParam(name = "size", defaultValue = "10")
                                            @Positive Integer size) {
        List<BookingDto> result;
        State stateFromString;
        try {
            stateFromString = Enum.valueOf(State.class, state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестный state");
        }
        result = bookingService.findAllByBooker(bookerId, stateFromString, from, size);
        log.info("Get all booking by bookerId={} page ={}", bookerId, from);
        return result;
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(name = "from", defaultValue = "0")
                                           @PositiveOrZero Integer from,
                                           @RequestParam(name = "size", defaultValue = "10")
                                           @Positive Integer size) {
        List<BookingDto> result;
        State stateFromString;
        try {
            stateFromString = Enum.valueOf(State.class, state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестный state");
        }
        result = bookingService.findAllByOwner(ownerId, stateFromString, from, size);
        log.info("Get all booking by ownerId={}", ownerId);
        return result;
    }


}

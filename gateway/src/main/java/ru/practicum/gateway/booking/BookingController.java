package ru.practicum.gateway.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.BookingDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
	private BookingClient bookingClient;

	@Autowired
	public BookingController(BookingClient bookingClient) {
		this.bookingClient = bookingClient;
	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
										 @Valid @RequestBody BookingDtoRequest bookingDto) {
		log.info("Create booking by bookerId={} with startTime={}", bookerId, bookingDto.getStart());
		return bookingClient.create(bookerId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> changeStatus(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
								   @PathVariable Long bookingId, @RequestParam boolean approved) {
		log.info("Change status booking by ownerId={}", ownerId);
		return bookingClient.changeStatus(ownerId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findById(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
		log.info("Get booking by bookingId={}", bookingId);
		return bookingClient.findById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> findAllByBooker(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
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
		log.info("Get all booking by bookerId={} page ={}", bookerId, from);
		return bookingClient.getBookings(bookerId, stateFromString, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findAllByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
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
		log.info("Get all booking by ownerId={}", ownerId);
		return bookingClient.getBookingsForOwner(ownerId, stateFromString, from, size);
	}


}
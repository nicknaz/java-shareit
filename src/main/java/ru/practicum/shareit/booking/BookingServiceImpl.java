package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.repository.BookingRepositoryJPA;
import ru.practicum.shareit.exception.ConflictIdException;
import ru.practicum.shareit.exception.InvalidDateException;
import ru.practicum.shareit.exception.NotFoundedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJPA;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private BookingRepositoryJPA bookingRepository;
    private UserRepositoryJPA userRepository;
    private ItemRepositoryJPA itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepositoryJPA bookingRepository,
                              UserRepositoryJPA userRepository,
                              ItemRepositoryJPA itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingDto create(BookingDtoRequest bookingDto, Long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundedException("Предмет не найден"));

        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundedException("Пользователь не найден"));

        if (item.getOwner().getId() == bookerId) {
            throw new ConflictIdException("Бронь своего же предмета невозможна!");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Предмет не доступен для бронирования!");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
            || bookingDto.getEnd().isBefore(bookingDto.getStart())
            || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new InvalidDateException("Некорректно выставлена дата бронирования!");
        }

        bookingDto.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(
                bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user)));
    }

    @Override
    public BookingDto changeStatus(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundedException("Бронирование не найден"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundedException("Предмет не найден"));

        if (item.getOwner().getId() != ownerId) {
            throw new NotFoundedException("Вы не являетесь владельцем предмета!");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронь уже подтверждена!");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundedException("Бронирование не найден"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundedException("Предмет не найден"));


        if (item.getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new NotFoundedException("Вы не являетесь владельцем предмета или создателем бронирования!");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllByBooker(Long bookerId, State state, Integer from, Integer size) {
        List<Booking> result;
        LocalDateTime date = LocalDateTime.now();
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundedException("Пользователь не найден"));

        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, PageRequest.of(from / size, size));
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, date, PageRequest.of(from / size, size));
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, date, PageRequest.of(from / size, size));
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                                                                                                        date,
                                                                                                        date,
                                                                                PageRequest.of(from / size, size));
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING, PageRequest.of(from / size, size));
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED, PageRequest.of(from / size, size));
                break;
            default:
                return null;
        }

        return result.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwner(Long ownerId, State state, Integer from, Integer size) {
        List<Booking> result;
        LocalDateTime date = LocalDateTime.now();
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundedException("Пользователь не найден"));

        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, PageRequest.of(from / size, size));
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, date, PageRequest.of(from / size, size));
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, date, PageRequest.of(from / size, size));
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                        date, date, PageRequest.of(from / size, size));
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, PageRequest.of(from / size, size));
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, PageRequest.of(from / size, size));
                break;
            default:
                return null;
        }

        return result.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

}

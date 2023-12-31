package ru.practicum.server.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingMapper;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.booking.dto.BookingDtoForItem;
import ru.practicum.server.booking.repository.BookingRepositoryJPA;
import ru.practicum.server.exception.InvalidDateException;
import ru.practicum.server.exception.NotFoundedException;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemDtoWithDates;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.CommentRepositoryJPA;
import ru.practicum.server.item.repository.ItemRepositoryJPA;
import ru.practicum.server.request.repository.RequestRepositoryJPA;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepositoryJPA;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepositoryJPA itemRepository;
    private BookingRepositoryJPA bookingRepository;
    private UserRepositoryJPA userRepository;
    private CommentRepositoryJPA commentRepository;
    private RequestRepositoryJPA requestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryJPA itemRepository,
                           UserRepositoryJPA userRepository,
                           BookingRepositoryJPA bookingRepository,
                           CommentRepositoryJPA commentRepository,
                           RequestRepositoryJPA requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional
    public ItemDto add(ItemDto item, Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundedException("Пользователь не найден"));
        return ItemMapper.toItemDto(itemRepository.save(
                ItemMapper.toItem(item, user,
                        item.getRequestId() != null ? requestRepository.findById(item.getRequestId()).get() : null)));
    }

    @Override
    public List<ItemDtoWithDates> getAllByOwner(Long ownerId) {
        List<ItemDtoWithDates> result = itemRepository.findAllByOwnerIdOrderById(ownerId)
                .stream()
                .map(x -> ItemMapper.toItemDtoWithDates(
                        x,
                        bookingRepository
                                .findAllByOwnerIdAndItemIdAndEndBeforeOrderByStartDesc(ownerId,
                                        x.getId(), LocalDateTime.now())
                                .stream()
                                .filter(k -> k.getStatus() == BookingStatus.APPROVED)
                                .map(BookingMapper::toBookingDtoForItem)
                                .findFirst().orElse(null),
                        bookingRepository
                                .findAllByItemOwnerIdAndItemIdAndStartAfterOrderByStartDesc(ownerId,
                                        x.getId(), LocalDateTime.now())
                                .stream()
                                .filter(k -> k.getStatus() == BookingStatus.APPROVED)
                                .map(BookingMapper::toBookingDtoForItem)
                                .reduce((a, b) -> b).orElse(null),
                        commentRepository.findAllByItemId(x.getId())))
                .collect(Collectors.toList());


        return result;
    }

    @Override
    public ItemDtoWithDates getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundedException("Предмет не найден"));

        BookingDtoForItem next =
                (userId == item.getOwner().getId()) ? bookingRepository
                        .findAllByItemOwnerIdAndItemIdAndStartAfterOrderByStartDesc(userId, itemId, LocalDateTime.now())
                        .stream()
                        .filter(x -> x.getStatus() == BookingStatus.APPROVED)
                        .map(BookingMapper::toBookingDtoForItem)
                        .reduce((a, b) -> b).orElse(null) : null;

        BookingDtoForItem last = (userId == item.getOwner().getId()) ? bookingRepository
                .findAllByOwnerIdAndItemIdAndEndBeforeOrderByStartDesc(userId, itemId, LocalDateTime.now())
                .stream()
                .filter(x -> x.getStatus() == BookingStatus.APPROVED)
                .map(BookingMapper::toBookingDtoForItem)
                .findFirst().orElse(null)
                : null;

        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return ItemMapper.toItemDtoWithDates(item, last, next, comments);
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto item, Long ownerId) {
        Item updItem = itemRepository.findById(id).orElseThrow(() -> new NotFoundedException("Предмет не найден"));

        if (updItem.getOwner().getId() != ownerId) {
            throw new NotFoundedException("Вы не можете изменить эту вещь, так как не вы её владелец!");
        }

        if (item.getName() != null && !item.getName().isBlank()) {
            updItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updItem.setAvailable(item.getAvailable());
        }


        updItem.setId(id);
        return ItemMapper.toItemDto(itemRepository.save((updItem)));
    }

    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.findAll()
                .stream()
                .filter(Item::getAvailable)
                .map(x -> Item.builder()
                        .id(x.getId())
                        .name(x.getName().toLowerCase())
                        .description(x.getDescription().toLowerCase())
                        .build())
                .filter(x -> (x.getName().contains(text.toLowerCase())
                        || x.getDescription().contains(text.toLowerCase())))
                .map(x -> itemRepository.findById(x.getId()).orElse(null))
                .filter(Objects::nonNull)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Comment createComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> bookingList =
                bookingRepository.findAllByBookerIdAndItemIdAndStatusNotAndStartBeforeOrderByStartDesc(userId,
                        itemId, BookingStatus.REJECTED, LocalDateTime.now(), PageRequest.of(0, 10));

        if (bookingList.size() == 0) {
            throw new InvalidDateException("Вы не можете оставлять комментарии под этим предметом," +
                    " так как не бронировали ее");
        }

        User user = userRepository.findById(userId).get();
        Item item = itemRepository.findById(itemId).get();
        return commentRepository.save(CommentMapper.toComment(commentDto, user, item));
    }
}
package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepositoryJPA;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepositoryJPA;
import ru.practicum.shareit.item.repository.ItemRepositoryJPA;
import ru.practicum.shareit.request.repository.RequestRepositoryJPA;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplMokTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepositoryJPA itemRepository;
    @Mock
    BookingRepositoryJPA bookingRepository;
    @Mock
    UserRepositoryJPA userRepository;
    @Mock
    CommentRepositoryJPA commentRepository;
    @Mock
    RequestRepositoryJPA requestRepository;


    @Test
    public void searchTest() {
        List<Item> itemList = Arrays.asList(
                Item.builder().id(1).name("exet").description("desc").available(true).build(),
                Item.builder().id(2).name("aeXb").description("desc").available(true).build(),
                Item.builder().id(3).name("treb").description("desc").available(true).build()
        );

        Mockito.when(itemRepository.findAll()).thenReturn(
                itemList);

        Mockito.when(itemRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0, Long.class);
            return Optional.of(itemList.get((Math.toIntExact(id-1))));
        });

        assertEquals(itemService.search("ex").size(), 2);
        assertEquals(itemService.search("rEb").size(), 1);
    }
}

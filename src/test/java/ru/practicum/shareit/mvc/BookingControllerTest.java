package ru.practicum.shareit.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateBooking() throws Exception {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setItemId(1L);
        bookingDtoRequest.setStart(LocalDateTime.now().plusMinutes(10));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusHours(2));
        bookingDtoRequest.setStatus(BookingStatus.WAITING);

        User user = User.builder()
                .id(1L)
                .name("Bob")
                .email("email@gmail.com")
                .build();

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(Item.builder()
                .name("name")
                .description("fve")
                .owner(user)
                .available(true)
                .id(1L)
                .build());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setBooker(user);
        bookingDto.setStatus(BookingStatus.WAITING);

        when(bookingService.create(any(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    public void testChangeStatus() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(new Item());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setBooker(new User());
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", 1L)
                .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void testFindById() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(new Item());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setBooker(new User());
        bookingDto.setStatus(BookingStatus.WAITING);

        when(bookingService.findById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    public void testFindAllBookings() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setItem(new Item());
        bookingDto1.setStart(LocalDateTime.now());
        bookingDto1.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto1.setBooker(new User());
        bookingDto1.setStatus(BookingStatus.WAITING);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setItem(new Item());
        bookingDto2.setStart(LocalDateTime.now());
        bookingDto2.setEnd(LocalDateTime.now().plusHours(3));
        bookingDto2.setBooker(new User());
        bookingDto2.setStatus(BookingStatus.APPROVED);

        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.findAllByBooker(anyLong(), any(), any(), any())).thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item").exists())
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].booker").exists())
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].item").exists())
                .andExpect(jsonPath("$[1].start").exists())
                .andExpect(jsonPath("$[1].end").exists())
                .andExpect(jsonPath("$[1].booker").exists())
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }
    
    


}
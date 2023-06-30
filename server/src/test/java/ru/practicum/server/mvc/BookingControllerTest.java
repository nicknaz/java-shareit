package ru.practicum.server.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.booking.BookingController;
import ru.practicum.server.booking.BookingService;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingDtoRequest;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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

    private BookingDto bookingDto1;

    private BookingDto bookingDto2;

    private User user;

    private User booker;

    private User owner;

    private Item item;

    private BookingDtoRequest bookingDtoRequest;

    @BeforeEach
    void setUp() {
        bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setItem(new Item());
        bookingDto1.setStart(LocalDateTime.now());
        bookingDto1.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto1.setBooker(new User());
        bookingDto1.setStatus(BookingStatus.WAITING);

        bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setItem(new Item());
        bookingDto2.setStart(LocalDateTime.now());
        bookingDto2.setEnd(LocalDateTime.now().plusHours(3));
        bookingDto2.setBooker(new User());
        bookingDto2.setStatus(BookingStatus.APPROVED);

        user = User.builder()
                .id(1L)
                .name("Bob")
                .email("email@gmail.com")
                .build();

        bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setItemId(1L);
        bookingDtoRequest.setStart(LocalDateTime.now().plusMinutes(10));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusHours(2));
        bookingDtoRequest.setStatus(BookingStatus.WAITING);

        booker = new User(1L, "booker", "booker@gmail.com");

        owner = new User(2L, "owner", "owner@gmail.com");

        item = new Item(1L, "item", "description", true, owner, null);

    }

    @Test
    public void testCreateBooking() throws Exception {

        when(bookingService.create(any(), anyLong())).thenReturn(bookingDto1);

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
        bookingDto1.setStatus(BookingStatus.APPROVED);
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto1);

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
        when(bookingService.findById(anyLong(), anyLong())).thenReturn(bookingDto1);

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

    @Test
    public void shouldBookingsUpdateWithApprovedFalse() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;


        bookingDtoRequest.setStatus(BookingStatus.REJECTED);
        bookingDto1.setStatus(BookingStatus.REJECTED);

        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto1);

        mockMvc.perform(patch("/bookings/{bookingId}?approved=false", bookingId)
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

    }

    @Test
    public void shouldBookingsAllReservationOwner() throws Exception {
        Integer userId = 2;


        LocalDateTime start = LocalDateTime.now().plusMinutes(1).withNano(000);
        LocalDateTime end = start.plusDays(1).withNano(000);


        when(bookingService.findAllByBooker(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
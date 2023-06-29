package ru.practicum.shareit.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private User owner;

    private User requestor;

    private ItemRequestDtoForResponse dtoForResponse;

    private ItemRequestDto dtoRequest;

    @BeforeEach
    void setUp() {
        owner = User
                .builder()
                .id(2)
                .name("name")
                .email("email@gmail.com")
                .build();

        requestor = User
                .builder()
                .id(2)
                .name("name")
                .email("email@gmail.com")
                .build();

        dtoRequest = ItemRequestDto.builder()
                .description("description")
                .build();

        dtoForResponse = ItemRequestDtoForResponse
                .builder()
                .id(1L)
                .description("description")
                .items(new ArrayList<>())
                .created(LocalDateTime.now())
                .build();

    }

    @Test
    public void testCreateRequest() throws Exception {
        when(itemRequestService.create(any(), anyLong())).thenReturn(dtoForResponse);

        String body = objectMapper.writeValueAsString(dtoRequest);

        mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void testReturnClientErrorWhenGetItemRequestWithoutUser() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testReturnEmptyItemRequestListWhenGetItemRequestWithoutRequest() throws Exception {
        Long userId = 1L;
        when(itemRequestService.getForUser(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testReturnEmptyItemRequestListWhenGetItemRequestWithoutPaginationParams() throws Exception {
        Long userId = 1L;
        when(itemRequestService.getForUser(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


}
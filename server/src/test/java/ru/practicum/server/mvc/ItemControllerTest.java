package ru.practicum.server.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.item.ItemController;
import ru.practicum.server.item.ItemService;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemDtoWithDates;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;

    private ItemDto createdItemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        createdItemDto = new ItemDto();
        createdItemDto.setId(1L);
        createdItemDto.setName("Item");
        createdItemDto.setDescription("Description");
        createdItemDto.setAvailable(true);
        createdItemDto.setRequestId(1L);
    }

    @Test
    public void testCreateItem() throws Exception {

        Mockito.when(itemService.add(Mockito.any(ItemDto.class), anyLong())).thenReturn(createdItemDto);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1));
    }

    @Test
    public void testGetAllItemsByOwner() throws Exception {
        List<ItemDtoWithDates> itemList = Arrays.asList(
                new ItemDtoWithDates(1L, "Item 1", "Description 1", true, null, null, null, Collections.emptyList()),
                new ItemDtoWithDates(2L, "Item 2", "Description 2", true, null, null, null, Collections.emptyList())
        );

        Mockito.when(itemService.getAllByOwner(anyLong())).thenReturn(itemList);

        mockMvc.perform(get("/items")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].request").doesNotExist())
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Item 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].available").value(true))
                .andExpect(jsonPath("$[1].request").doesNotExist());
    }

    @Test
    public void testGetItemById() throws Exception {
        ItemDtoWithDates itemDto = new ItemDtoWithDates(1L, "Item", "Description", true, null, null, null, Collections.emptyList());

        Mockito.when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", 1L)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.request").doesNotExist());
    }

    @Test
    public void testUpdateItem() throws Exception {
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");

        createdItemDto.setName("Updated Item");
        createdItemDto.setDescription("Updated Description");

        Mockito.when(itemService.update(anyLong(), any(), anyLong())).thenReturn(createdItemDto);

        mockMvc.perform(patch("/items/{id}", 1L)
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1));
    }

    @Test
    public void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/{id}", 1L)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1)).delete(1L);
    }

}
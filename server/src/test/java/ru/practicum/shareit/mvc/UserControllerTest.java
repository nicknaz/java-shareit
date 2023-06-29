package ru.practicum.shareit.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto
                .builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.add(any())).thenReturn(userDto);

        String jsonUser = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email@gmail.com"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getById(any())).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email@gmail.com"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAll()).thenReturn(List.of(userDto, userDto, userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));
    }

    @Test
    void testUpdateUserWithPatchWhenStatusIs200() throws Exception {
        UserDto updatedUser = new UserDto(1L, "nameUpd", "emailUpd@gmail.com");
        String jsonUser = objectMapper.writeValueAsString(updatedUser);

        when(userService.update(anyLong(), any())).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("emailUpd@gmail.com"))
                .andExpect(jsonPath("$.name").value("nameUpd"));
    }

    @Test
    void testUpdateUserNameWithPatch() throws Exception {
        String jsonUser = "{\"name\":\"updName\"}";

        when(userService.update(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserEmailWithPatch() throws Exception {
        String jsonUser = "{\"email\":\"updName@user.com\"}";

        when(userService.update(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

}

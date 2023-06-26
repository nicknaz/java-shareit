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
        userDto = new UserDto(1L, "user", "user@user.com");
    }

    @Test
    void shouldCreateUser() throws Exception {
        when(userService.add(any())).thenReturn(userDto);

        String jsonUser = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(userService.getById(any())).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        when(userService.getAll()).thenReturn(List.of(userDto, userDto, userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));
    }

    @Test
    void shouldReturnClientErrorWhenAddingUserWithEmptyName() throws Exception {
        UserDto user = new UserDto(2L, "", "user@user.com");
        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnClientErrorWhenAddingUserWithEmptyEmail() throws Exception {
        UserDto user = new UserDto(2L, "user", "");
        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldUpdateUserWithPatchWhenStatusIs200() throws Exception {
        UserDto updatedUser = new UserDto(1L, "update", "update@user.com");
        String jsonUser = objectMapper.writeValueAsString(updatedUser);

        when(userService.update(anyLong(), any())).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("update@user.com"))
                .andExpect(jsonPath("$.name").value("update"));
    }

    @Test
    void shouldUpdateUserNameWithPatch() throws Exception {
        String jsonUser = "{\"name\":\"updateName\"}";

        when(userService.update(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateUserEmailWithPatch() throws Exception {
        String jsonUser = "{\"email\":\"updateName@user.com\"}";

        when(userService.update(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

}

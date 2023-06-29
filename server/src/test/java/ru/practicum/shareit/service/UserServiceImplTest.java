package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundedException;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepositoryJPA userRepository;

    @Test
    void add() {
        UserDto userDto = UserDto
                .builder()
                .email("email@gmail.com")
                .name("Name")
                .build();

        UserDto userDtoResponse = userService.add(userDto);

        assertNotNull(userDtoResponse.getId());
        assertEquals(userDto.getEmail(), userDtoResponse.getEmail());
        assertEquals(userDto.getName(), userDtoResponse.getName());

        User userDB = userRepository.findById(userDtoResponse.getId()).orElse(null);
        assertNotNull(userDB);
        assertEquals(userDto.getEmail(), userDB.getEmail());
        assertEquals(userDto.getName(), userDB.getName());
    }

    @Test
    public void testGetAll() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("John");
        userDto1.setEmail("john@example.com");
        userService.add(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("Jane");
        userDto2.setEmail("jane@example.com");
        userService.add(userDto2);

        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    public void testGetById() {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");
        UserDto savedUser = userService.add(userDto);

        UserDto result = userService.getById(savedUser.getId());

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    public void testGetByIdNotFound() {
        assertThrows(NotFoundedException.class, () -> userService.getById(1234L));
    }

    @Test
    public void testUpdate() {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");
        UserDto savedUser = userService.add(userDto);

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("Updated John");
        updatedUserDto.setEmail("updatedjohn@example.com");

        UserDto result = userService.update(savedUser.getId(), updatedUserDto);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals("Updated John", result.getName());
        assertEquals("updatedjohn@example.com", result.getEmail());
    }

    @Test
    public void testUpdateWithoutName() {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");
        UserDto savedUser = userService.add(userDto);

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName(null);
        updatedUserDto.setEmail("updatedjohn@example.com");

        UserDto result = userService.update(savedUser.getId(), updatedUserDto);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals("John", result.getName());
        assertEquals("updatedjohn@example.com", result.getEmail());

        updatedUserDto = new UserDto();
        updatedUserDto.setName("");
        updatedUserDto.setEmail("updatedjohn2@example.com");

        result = userService.update(savedUser.getId(), updatedUserDto);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals("John", result.getName());
        assertEquals("updatedjohn2@example.com", result.getEmail());
    }

    @Test
    public void testUpdateWithoutEmail() {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");
        UserDto savedUser = userService.add(userDto);

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("Bob");
        updatedUserDto.setEmail(null);

        UserDto result = userService.update(savedUser.getId(), updatedUserDto);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals("Bob", result.getName());
        assertEquals("john@example.com", result.getEmail());

        updatedUserDto = new UserDto();
        updatedUserDto.setName("Bob2");
        updatedUserDto.setEmail("");

        result = userService.update(savedUser.getId(), updatedUserDto);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals("Bob2", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    public void testUpdateNotFound() {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        assertThrows(NotFoundedException.class, () -> userService.update(1234L, userDto));
    }

    @Test
    public void testDelete() {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setEmail("john@example.com");
        UserDto savedUser = userService.add(userDto);

        userService.delete(savedUser.getId());

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }
}
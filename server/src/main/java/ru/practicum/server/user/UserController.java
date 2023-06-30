package ru.practicum.server.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        UserDto newUser = userService.add(user);
        log.info("Create user with userId={}", newUser.getId());
        return newUser;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Get all users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") Long userId) {
        log.info("Get user by userId={}", userId);
        return userService.getById(userId);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId,
                          @RequestBody UserDto dto) {
        log.info("Update user by userId={}", userId);
        return userService.update(userId, dto);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") Long userId) {
        log.info("Delete user by userId={}", userId);
        userService.delete(userId);
        return HttpStatus.OK;
    }
}

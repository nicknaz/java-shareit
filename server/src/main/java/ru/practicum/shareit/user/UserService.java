package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto user);

    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto update(Long id, UserDto user);

    void delete(Long id);
}

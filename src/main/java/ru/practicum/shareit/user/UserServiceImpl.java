package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto add(UserDto user) {
        return UserMapper.toUserDto(userRepository.add(UserMapper.toUser(user)));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userRepository.getById(id));
    }

    @Override
    public UserDto update(Long id, UserDto user) {
        return UserMapper.toUserDto(userRepository.update(id, UserMapper.toUser(user)));
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }
}

package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundedException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private UserRepositoryJPA userRepository;

    @Autowired
    public UserServiceImpl(UserRepositoryJPA userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto add(UserDto user) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundedException("Пользователь не найден"));
        return UserMapper.toUserDto(userRepository.findById(id).get());
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto user) {
        User updUser = userRepository.findById(id).orElseThrow(() -> new NotFoundedException("Пользователь не найден"));
        user.setId(id);
        if (user.getName() != null && !user.getName().isBlank()) {
            updUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(updUser));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}

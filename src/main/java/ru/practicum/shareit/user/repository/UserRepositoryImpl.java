package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundedException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private long idGenerator = 1;
    private HashMap<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        if (users.values()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList())
                .contains(user.getEmail())) {
            throw new EmailAlreadyExistException("Пользователь с таким email уже существует!");
        }
        user.setId(idGenerator);
        users.put(idGenerator++, user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundedException("Пользователь с таким id не найден");
        }
        return users.get(id);
    }

    @Override
    public User update(Long id, User user) {
        if (!users.containsKey(id)) {
            throw new NotFoundedException("Пользователь с таким id не найден");
        }
        if (users.values()
                .stream()
                .filter(x -> x.getId() != id)
                .map(User::getEmail)
                .collect(Collectors.toList())
                .contains(user.getEmail())) {
            throw new EmailAlreadyExistException("Пользователь с таким email уже существует!");
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            users.get(id).setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            users.get(id).setEmail(user.getEmail());
        }
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}

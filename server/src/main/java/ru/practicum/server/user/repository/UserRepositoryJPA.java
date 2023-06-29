package ru.practicum.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.user.model.User;

import java.util.List;

@Repository
public interface UserRepositoryJPA extends JpaRepository<User, Long> {
    List<User> findAllByEmail(String email);
}

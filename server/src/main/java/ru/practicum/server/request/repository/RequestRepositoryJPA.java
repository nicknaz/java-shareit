package ru.practicum.server.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.user.model.User;

import java.util.List;

@Repository
public interface RequestRepositoryJPA extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorOrderByCreatedDateDesc(User user);

    List<ItemRequest> findAllByRequestorIsNotOrderByCreatedDateDesc(User user, Pageable page);
}

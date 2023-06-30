package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepositoryJPA extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(Long id);

    List<Item> findAllByRequestId(Long id);
}

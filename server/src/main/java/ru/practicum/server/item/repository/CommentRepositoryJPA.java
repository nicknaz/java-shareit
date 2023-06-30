package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepositoryJPA extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);
}

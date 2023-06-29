package ru.practicum.gateway.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.model.Comment;
import ru.practicum.gateway.item.model.Item;
import ru.practicum.gateway.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment, User author) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .item(comment.getItem())
                .authorName(author.getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
    }

    public static Comment toComment(CommentDto dto, User author, Item item) {
        return Comment.builder()
                .authorName(author.getName())
                .created(LocalDateTime.now())
                .text(dto.getText())
                .item(item)
                .build();
    }
}

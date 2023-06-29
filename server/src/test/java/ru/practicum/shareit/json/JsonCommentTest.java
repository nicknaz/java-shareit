package ru.practicum.shareit.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class JsonCommentTest {
    @Autowired
    private JacksonTester<Comment> jsonComment;

    @Autowired
    private JacksonTester<CommentDto> jsonCommentDto;

    private LocalDateTime created;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now().plusDays(1);
    }

    @Test
    void testSerializeAndDeserializeComment() throws Exception {
        Comment comment = new Comment(1L, "comm", new Item(), "name", created);
        CommentDto commentDto = CommentMapper.toCommentDto(comment, new User());

        JsonContent<CommentDto> serializedCommentDto = jsonCommentDto.write(commentDto);

        assertThat(serializedCommentDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(serializedCommentDto).extractingJsonPathStringValue("$.text").isEqualTo("comm");
        assertThat(serializedCommentDto).extractingJsonPathValue("$.created").isNotNull();
    }

    @Test
    void testSerializeAndDeserializeCommentResponseDto() throws Exception {
        Comment comment = new Comment(1L, "commу",
                new Item(2L, "item", "description",
                         true, new User(), null), "name", created);

        CommentDto commentDto = CommentMapper.toCommentDto(comment, User.builder().name("name").build());

        JsonContent<CommentDto> serializedCommentDto = jsonCommentDto.write(commentDto);

        assertThat(serializedCommentDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(serializedCommentDto).extractingJsonPathStringValue("$.text").isEqualTo("commу");
        assertThat(serializedCommentDto).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(serializedCommentDto).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        assertThat(serializedCommentDto).extractingJsonPathValue("$.created").isNotNull();
    }
}

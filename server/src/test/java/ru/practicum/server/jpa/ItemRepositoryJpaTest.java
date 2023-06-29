package ru.practicum.server.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepositoryJPA;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepositoryJPA;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryJpaTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepositoryJPA itemRepository;

    @Autowired
    private UserRepositoryJPA userRepository;

    @Test
    void testItemSaved() throws Exception {
        LocalDateTime time = LocalDateTime.now();

        User user = new User(1L, "user1", "email1@gmail.com");
        User userInRep = userRepository.save(user);

        Item item = new Item(1L, "item", "description", true, userInRep, null);
        Item itemInRep = itemRepository.save(item);

        Long savedItemId = itemInRep.getId();

        Item retrievedItem = entityManager.find(Item.class, savedItemId);

        assertThat(retrievedItem).isEqualTo(itemInRep);
    }
}

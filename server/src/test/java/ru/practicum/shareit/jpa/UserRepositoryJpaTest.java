package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryJpaTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepositoryJPA userRepository;

    @Test
    void testUserDto() throws Exception {
        LocalDateTime time = LocalDateTime.now();

        User user = new User(2L, "user", "email@gmail.com");
        User userInRep = userRepository.save(user);

        User retrievedUser = entityManager.find(User.class, userInRep.getId());

        assertThat(retrievedUser).isEqualTo(userInRep);
    }

}

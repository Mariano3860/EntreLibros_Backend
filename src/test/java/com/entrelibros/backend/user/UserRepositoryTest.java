package com.entrelibros.backend.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void createAndRetrieveUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("new@entrelibros.com");
        user.setPassword("pw");
        user.setRole(User.Role.USER);

        User saved = userRepository.save(user);

        assertTrue(userRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void listUsers() {
        List<User> users = userRepository.findAll();
        assertTrue(users.size() >= 5);
    }

    @Test
    void updateUser() {
        User user = userRepository.findByEmail("user@entrelibros.com").orElseThrow();
        user.setEmail("updated@entrelibros.com");
        userRepository.save(user);

        assertEquals("updated@entrelibros.com", userRepository.findById(user.getId()).orElseThrow().getEmail());
    }

    @Test
    void deleteUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("todelete@entrelibros.com");
        user.setPassword("pw");
        user.setRole(User.Role.USER);
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        assertFalse(userRepository.findById(saved.getId()).isPresent());
    }
}


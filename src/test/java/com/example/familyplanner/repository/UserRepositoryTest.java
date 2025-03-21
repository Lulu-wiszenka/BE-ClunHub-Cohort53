package com.example.familyplanner.repository;

import com.example.familyplanner.controller.TestConfig;
import com.example.familyplanner.entity.Role;
import com.example.familyplanner.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role userRole;
    private Role adminRole;
    private User user1;
    private User user2;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Создаем роли
        userRole = new Role("USER");
        adminRole = new Role("ADMIN");

        entityManager.persist(userRole);
        entityManager.persist(adminRole);

        // Создаем пользователей
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setRole(userRole);

        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setRole(userRole);

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("adminpass");
        adminUser.setRole(adminRole);

        // Сохраняем пользователей
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(adminUser);

        entityManager.flush();
    }

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        Optional<User> found = userRepository.findByEmail("user1@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("user1");
        assertThat(found.get().getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        Optional<User> found = userRepository.findByUsername("user2");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("user2");
        assertThat(found.get().getEmail()).isEqualTo("user2@example.com");
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void findByRole_shouldReturnUsersWithRole() {
        List<User> usersWithUserRole = userRepository.findByRole(userRole);
        List<User> usersWithAdminRole = userRepository.findByRole(adminRole);

        assertThat(usersWithUserRole.size()).isEqualTo(2);
        assertThat(usersWithUserRole).extracting(User::getUsername).containsExactlyInAnyOrder("user1", "user2");

        assertThat(usersWithAdminRole.size()).isEqualTo(1);
        assertThat(usersWithAdminRole).extracting(User::getUsername).containsExactly("admin");
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        UUID userId = user1.getId();
        Optional<User> found = userRepository.findById(userId);

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("user1");
        assertThat(found.get().getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    void findById_shouldReturnEmpty_whenUserDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<User> found = userRepository.findById(nonExistentId);

        assertThat(found).isEmpty();
    }

    @Test
    void saveUser_shouldPersistUser() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("newpass");
        newUser.setRole(userRole);

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();

        // Проверяем что пользователь действительно сохранился в базе
        Optional<User> found = userRepository.findByEmail("newuser@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("newuser");
    }
}
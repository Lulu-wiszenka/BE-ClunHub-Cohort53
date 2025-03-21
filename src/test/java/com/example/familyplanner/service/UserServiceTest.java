package com.example.familyplanner.service;

import com.example.familyplanner.Security.JWT.UserDetailImpl;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnUserDetailsWhenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetailImpl userDetails = (UserDetailImpl) userService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals(userId, userDetails.getId());
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encoded_password", userDetails.getPassword());

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("notfound@example.com");
        });

        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }
}
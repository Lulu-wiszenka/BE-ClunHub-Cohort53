package com.example.familyplanner.service;

import com.example.familyplanner.dto.UserResponseDto;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.converter.UserConverter;
import com.example.familyplanner.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class FindUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter converter;

    @InjectMocks
    private FindUserService findUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll_shouldReturnListOfUserResponseDto() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        User user2 = new User();
        user2.setId(UUID.randomUUID());

        UserResponseDto dto1 = new UserResponseDto();
        dto1.setId(user1.getId());
        UserResponseDto dto2 = new UserResponseDto();
        dto2.setId(user2.getId());

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(converter.createDtoFromUser(user1)).thenReturn(dto1);
        when(converter.createDtoFromUser(user2)).thenReturn(dto2);

        List<UserResponseDto> result = findUserService.findAll();

        assertEquals(2, result.size());
        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());
        verify(userRepository, times(1)).findAll();
        verify(converter, times(1)).createDtoFromUser(user1);
        verify(converter, times(1)).createDtoFromUser(user2);
    }

    @Test
    void findUserByEmail_shouldReturnDto_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        UserResponseDto dto = new UserResponseDto();
        dto.setId(userId);
        dto.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(converter.createDtoFromUser(user)).thenReturn(dto);

        UserResponseDto result = findUserService.findUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(converter, times(1)).createDtoFromUser(user);
    }

    @Test
    void findUserByEmail_shouldThrowNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> findUserService.findUserByEmail("notfound@example.com"));
        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    void findAllFullDetails_shouldReturnAllUsers() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        User user2 = new User();
        user2.setId(UUID.randomUUID());

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = findUserService.findAllFullDetails();

        assertEquals(2, result.size());
        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());
        verify(userRepository, times(1)).findAll();
    }
}
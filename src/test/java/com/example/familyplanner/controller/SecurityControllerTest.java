package com.example.familyplanner.controller;

import com.example.familyplanner.Security.JWT.JwtCore;
import com.example.familyplanner.dto.LoginRequest;
import com.example.familyplanner.dto.RegistrationRequest;
import com.example.familyplanner.dto.UserResponseDto;
import com.example.familyplanner.entity.Role;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.RegisterUserService;
import com.example.familyplanner.service.exception.AlreadyExistException;
import com.example.familyplanner.service.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SecurityControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegisterUserService registerUserService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtCore jwtCore;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SecurityController securityController;

    private LoginRequest validLoginRequest;
    private RegistrationRequest validRegistrationRequest;
    private UserResponseDto userResponseDto;
    private UUID testUserId;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();


        securityController = new SecurityController(
                userRepository,
                passwordEncoder,
                authenticationManager,
                jwtCore,
                registerUserService
        );


        mockMvc = MockMvcBuilders
                .standaloneSetup(securityController)
                .setControllerAdvice(new ExceptionHandler())
                .build();


        testUserId = UUID.randomUUID();

        validLoginRequest = new LoginRequest("test@example.com", "Test!123");

        validRegistrationRequest = new RegistrationRequest();
        validRegistrationRequest.setName("TestUser");
        validRegistrationRequest.setEmail("test@example.com");
        validRegistrationRequest.setPassword("Test!123");

        Role userRole = new Role();
        userRole.setId(UUID.randomUUID());
        userRole.setRoleName("USER");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(testUserId);
        userResponseDto.setUsername("Test User");
        userResponseDto.setEmail("test@example.com");
        userResponseDto.setRole(userRole);


    }

    @Test
    void authenticateUser_WithValidCredentials_ReturnsToken() throws Exception {

        when(jwtCore.createToken("test@example.com")).thenReturn("test.jwt.token");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));


        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test.jwt.token"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.message").value("Login successful"));


        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtCore).createToken("test@example.com");
    }

    @Test
    void authenticateUser_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequest invalidRequest = new LoginRequest("test@example.com", "WrongPassword");


        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerUser_WithValidData_ReturnsCreatedUser() throws Exception {

        when(registerUserService.createNewUser(any(RegistrationRequest.class))).thenReturn(userResponseDto);


        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.user.username").value("Test User"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.message").value("User successfully registered"))
                .andExpect(jsonPath("$.status").value("success"));


        verify(registerUserService).createNewUser(any(RegistrationRequest.class));
    }

    @Test
    void registerUser_WithExistingEmail_ReturnsBadRequest() throws Exception {

        when(registerUserService.createNewUser(any(RegistrationRequest.class)))
                .thenThrow(new AlreadyExistException("User with email test@example.com already exists"));


        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with email test@example.com already exists"));
    }

    @Test
    void registerUser_WithInvalidPassword_ReturnsBadRequest() throws Exception {

        when(registerUserService.createNewUser(any(RegistrationRequest.class)))
                .thenThrow(new ValidationException("Password does not meet security requirements"));


        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Password does not meet security requirements"));
    }

    @Test
    void registerUser_WithEmptyName_ReturnsBadRequest() throws Exception {

        RegistrationRequest invalidRequest = new RegistrationRequest();
        invalidRequest.setName("a");
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("Test!123");


        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WithInvalidEmailFormat_ReturnsBadRequest() throws Exception {

        RegistrationRequest invalidRequest = new RegistrationRequest();
        invalidRequest.setName("Test User");
        invalidRequest.setEmail("not-an-email");
        invalidRequest.setPassword("Test!123");


        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
package com.example.familyplanner.controller;

import com.example.familyplanner.Security.JWT.JwtCore;
import com.example.familyplanner.dto.LoginRequest;
import com.example.familyplanner.dto.RegistrationRequest;
import com.example.familyplanner.dto.UserResponseDto;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.RegisterUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for user authentication and registration")
public class SecurityController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    private final RegisterUserService registerUserService;

    @Autowired
    public SecurityController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                              AuthenticationManager authenticationManager, JwtCore jwtCore,
                              RegisterUserService registerUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
        this.registerUserService = registerUserService;
    }


    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user in the system and issues a unique JWT token for further authentication",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful login. JWT token returned in response"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                    @ApiResponse(responseCode = "400", description = "Invalid request (missing email or password)"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/sign-in")
  
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtCore.createToken(loginRequest.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("email", loginRequest.getEmail());
        response.put("message", "Login successful");

        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Register new user",
            description = "Registers a new user in the system for subsequent JWT token acquisition",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully registered"),
                    @ApiResponse(responseCode = "400", description = "Validation error or user with this email already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/sign-up")

    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {
        UserResponseDto newUser = registerUserService.createNewUser(request);

        Map<String, Object> response = new HashMap<>();
        response.put("user", newUser);
        response.put("message", "User successfully registered");
        response.put("status", "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
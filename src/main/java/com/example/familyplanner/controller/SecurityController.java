package com.example.familyplanner.controller;

import com.example.familyplanner.Security.JWT.JwtCore;
import com.example.familyplanner.dto.LoginRequest;
import com.example.familyplanner.dto.RegistrationRequest;
import com.example.familyplanner.dto.UserResponseDto;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.RegisterUserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SecurityController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;
    private RegisterUserService registerUserService;

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

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.createToken(authentication.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("username", authentication.getName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
        UserResponseDto newUser = registerUserService.createNewUser(request);

        Map<String, Object> response = new HashMap<>();
        response.put("user", newUser);
        response.put("message", "User succsessfully created");

        return ResponseEntity.ok(response);
    }
}
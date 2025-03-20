package com.example.familyplanner.controller;

import com.example.familyplanner.dto.RegistrationRequest;
import com.example.familyplanner.dto.UserResponseDto;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.service.FindUserService;
import com.example.familyplanner.service.RegisterUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final FindUserService findService;
    private final RegisterUserService registerService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createNewUser(@RequestBody RegistrationRequest request) {
        return new ResponseEntity<>(registerService.createNewUser(request), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<UserResponseDto> findAll() {
        return findService.findAll();
    }


    @GetMapping("/full")
    public List<User> findAllFullDetails() {
        return findService.findAllFullDetails();
    }

    @GetMapping("/email")
    public UserResponseDto findUserByEmail(@RequestParam String email) {
        return findService.findUserByEmail(email);
    }
}

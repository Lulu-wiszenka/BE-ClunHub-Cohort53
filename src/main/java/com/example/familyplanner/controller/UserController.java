package com.example.familyplanner.controller;

import com.example.familyplanner.dto.RegistrationRequest;
import com.example.familyplanner.dto.UserResponseDto;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.service.FindUserService;
import com.example.familyplanner.service.RegisterUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//нужны тесты
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final FindUserService findService;
    private final RegisterUserService registerService;

    @Operation(
            summary = "Create user",
                description = "Create user. Store him in DataBase, and return in response only non confidential info",
                    responses = {   @ApiResponse(responseCode = "201", description = "User successfully created"),
                                    @ApiResponse(responseCode = "400", description = "User already exist"),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                                    @ApiResponse(responseCode = "500", description = "Iternal Server Error")})
    @PostMapping
    public ResponseEntity<UserResponseDto> createNewUser(@Valid @RequestBody RegistrationRequest request) {
        return new ResponseEntity<>(registerService.createNewUser(request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Find all users",
                description = "Find all users that already stores in DataBase and return in response only non Confidential information",
                    responses = {   @ApiResponse(responseCode = "200", description = "All users found"),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                                    @ApiResponse(responseCode = "500", description = "Iternal Server Error")})
    @GetMapping
    public List<UserResponseDto> findAll() {
        return findService.findAll();
    }


    //хз зачем он нужен...
//    @Operation(
//            summary = "Find all users with all information",
//                description = "Find and return in response all users with all sort of information including confidential data",
//                    responses = {   @ApiResponse(responseCode = "200", description = "All users with full information found"),
//                                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
//                                    @ApiResponse(responseCode = "500", description = "Iternal Server Error")})
//    @GetMapping("/full")
//    public List<User> findAllFullDetails() {
//        return findService.findAllFullDetails();
//    }

    @Operation(
            summary = "Find user by email",
                description = "Find user that already exist in DataBase via his unique and personal email",
                    responses = {   @ApiResponse(responseCode = "200", description = "All users found"),
                                    @ApiResponse(responseCode = "400", description = "Validation Error"),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                                    @ApiResponse(responseCode = "404", description = "User not found"),
                                    @ApiResponse(responseCode = "500", description = "Iternal Server Error")})
    @GetMapping("/by-email/{email}")
    public UserResponseDto findUserByEmail(@PathVariable String email) {
        return findService.findUserByEmail(email);
    }
}

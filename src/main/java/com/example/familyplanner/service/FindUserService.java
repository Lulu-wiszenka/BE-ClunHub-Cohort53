package com.example.familyplanner.service;

import com.example.familyplanner.dto.UserResponseDto;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.RoleRepository;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.converter.UserConverter;
import com.example.familyplanner.service.exception.NotFoundException;
import com.example.familyplanner.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserConverter converter;
    private final ValidationService validation;

    public List<UserResponseDto> findAll(){
        return userRepository.findAll().stream()
                .map(manager -> converter.createDtoFromUser(manager))
                .toList();
    }

    public UserResponseDto findUserByEmail(String email){

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()){
            UserResponseDto response = converter.createDtoFromUser(userOptional.get());
            return response;
        } else {
            throw new NotFoundException("Manager with email " + email + " not found");
        }
    }

    public List<User> findAllFullDetails(){
        return userRepository.findAll();
    }

}

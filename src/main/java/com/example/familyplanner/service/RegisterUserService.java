package com.example.familyplanner.service;

import com.example.familyplanner.dto.RegistrationRequest;
import com.example.familyplanner.dto.UserResponseDto;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.RoleRepository;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.converter.UserConverter;
import com.example.familyplanner.service.exception.AlreadyExistException;
import com.example.familyplanner.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserConverter converter;
    private final ValidationService validation;


    public UserResponseDto createNewUser(RegistrationRequest request) {
        if(validation.userExists(request.getEmail())){
            throw new AlreadyExistException("User with " + request.getEmail() + " already exists");
        }

        User newUser = converter.createUserFromDto(request); //Хеширование пароля происходит внутри конвертера, на этапе приёма request
        User savedUser = userRepository.save(newUser);

        return converter.createDtoFromUser(savedUser);
    }


}

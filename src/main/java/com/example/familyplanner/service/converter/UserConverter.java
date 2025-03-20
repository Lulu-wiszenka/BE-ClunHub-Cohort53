package com.example.familyplanner.service.converter;

import com.example.familyplanner.dto.RegistrationRequest;
import com.example.familyplanner.dto.UserResponseDto;
import com.example.familyplanner.entity.Role;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.RoleRepository;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    public UserResponseDto createDtoFromUser(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    public User createUserFromDto(RegistrationRequest request) {
        User newUser = new User();
        newUser.setUsername(request.getName());
        newUser.setEmail(request.getEmail());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(hashedPassword);


        Optional<Role> defaultRole = roleRepository.findByRoleName("USER");


        if(defaultRole.isEmpty()) {
            Role userRole = new Role("USER");
            defaultRole = Optional.of(roleRepository.save(userRole));
        }


        newUser.setRole(defaultRole.get());
        return newUser;
    }

}

package com.example.familyplanner.service.validation;

import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ValidationService {

    private final UserRepository userRepository;

    public boolean userExists(String email) {
        Optional<User> existUser = userRepository.findByEmail(email);
        return existUser.isPresent();
    }
}

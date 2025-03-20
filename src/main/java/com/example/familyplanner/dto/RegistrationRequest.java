package com.example.familyplanner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
        @NotBlank
        private String name;

        @Email(message = "invalid Email format")
        @NotBlank
        private String email;

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$",
                message = "Password must contain at least one special character (!@#$%^&* etc.)")
        @Size(min = 8, message = "Password should have at least 8 symbols")
        @Size(max = 25, message = "Password cant be bigger than 25 symbols")
        private String password;
    }


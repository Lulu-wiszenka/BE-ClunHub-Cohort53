package com.example.familyplanner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//нужны тесты
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must not contain any special characters")
        //^ — начало строки
        //[a-zA-Z0-9]+ — только латинские буквы и цифры, не менее одного символа
        //$ — конец строки
        @Size(min = 2, message = "Username should have at least 2 symbols")
        @Size(max = 15, message = "Username can't be more than 15 symbols")
        private String name;

        @Email(message = "invalid Email format")
        @NotBlank
        @Size(max = 40)
        private String email;

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$",
                message = "Password must contain at least one special character (!@#$%^&* etc.)")
        @Size(min = 8, message = "Password should have at least 8 symbols")
        @Size(max = 25, message = "Password cant be bigger than 25 symbols")
        private String password;
    }


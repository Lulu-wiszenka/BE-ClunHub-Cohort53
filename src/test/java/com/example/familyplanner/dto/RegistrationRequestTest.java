package com.example.familyplanner.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
class RegistrationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest("JohnDoe", "john@example.com", "Password@1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty()); //violations.isEmpty() - проверяет, что ни одна из анотаций @NotBlank/@Email...
                                          // в классе RegistrationRequest не прошла, что значит об успешном создании объекта
    }

    @Test
    void testInvalidUsernameSpecialChars() {
        RegistrationRequest request = new RegistrationRequest("John*Doe", "john@example.com", "Password@1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Username must not contain any special characters"));
    }

// validator.validate(request) — возвращает список нарушений.
// assertEquals(1, violations.size()) — ожидаем ровно одно нарушение.
// assertTrue(violations.iterator().next().getMessage().contains("Username must not contain any special characters")) -
// Проверяем, что сообщение об ошибке содержит ожидаемый текст.

    @Test
    void testInvalidUsernameTooShort() {
        RegistrationRequest request = new RegistrationRequest("J", "john@example.com", "Password@1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Username should have at least 2 symbols"));
    }

    @Test
    void testInvalidUsernameTooLong() {
        RegistrationRequest request = new RegistrationRequest("JohnDoeJohnDoeJohn", "john@example.com", "Password@1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Username can't be more than 15 symbols"));
    }

    @Test
    void testInvalidEmailFormat() {
        RegistrationRequest request = new RegistrationRequest("JohnDoe", "invalid-email", "Password@1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("invalid Email format"));
    }

    @Test
    void testInvalidPasswordNoSpecialChar() {
        RegistrationRequest request = new RegistrationRequest("JohnDoe", "john@example.com", "Password1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Password must contain at least one special character"));
    }

    @Test
    void testInvalidPasswordTooShort() {
        RegistrationRequest request = new RegistrationRequest("JohnDoe", "john@example.com", "P@1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Password should have at least 8 symbols"));
    }

    @Test
    void testInvalidPasswordTooLong() {
        RegistrationRequest request = new RegistrationRequest("JohnDoe", "john@example.com", "P@sswordThatIsWayTooLong1234567");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Password cant be bigger than 25 symbols"));
    }

}
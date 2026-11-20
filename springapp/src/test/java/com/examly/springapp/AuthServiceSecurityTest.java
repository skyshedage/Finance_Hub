package com.examly.springapp;

import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepo;
import com.examly.springapp.security.JwtUtil;
import com.examly.springapp.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceSecurityTest {
    private UserRepo users;
    private JwtUtil jwt;
    private PasswordEncoder encoder;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        users = mock(UserRepo.class);
        jwt = mock(JwtUtil.class);
        encoder = mock(PasswordEncoder.class);
        auth = new AuthService(users, jwt);
        ReflectionTestUtils.setField(auth, "passwordEncoder", encoder);
    }

    @Test
    void publicRegistrationCannotSelectAdministratorRole() {
        User user = new User();
        user.setEmail("test@example.invalid");
        user.setPassword("test-only-input");
        user.setUserRole("ADMIN");
        when(users.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(encoder.encode(any())).thenReturn("test-only-hash");

        auth.register(user);

        assertEquals("USER", user.getUserRole());
        assertEquals("test-only-hash", user.getPassword());
        verify(users).save(user);
    }

    @Test
    void successfulLoginDoesNotPrintCredentials() {
        User user = new User();
        user.setEmail("test@example.invalid");
        user.setPassword("test-only-hash");
        user.setUserRole("USER");
        when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches("test-only-input", user.getPassword())).thenReturn(true);
        when(jwt.generateToken(user)).thenReturn("test-token");
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            auth.login(user.getEmail(), "test-only-input");
        } finally {
            System.setOut(original);
        }
        assertFalse(output.toString().contains("test-only-input"));
        assertFalse(output.toString().contains(user.getEmail()));
    }
}

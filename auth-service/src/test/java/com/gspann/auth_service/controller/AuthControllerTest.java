package com.gspann.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gspann.auth_service.jwt.AuthRequest;
import com.gspann.auth_service.model.User;
import com.gspann.auth_service.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // har test se pehle DB clean kar
    }

    @Test
    void testSignupSuccess() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("temp");
        user.setName("Test User");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        // pehle ek user manually add kar le DB me
        User user = new User();
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("abhaysingh"));
        user.setName("Test User");
        userRepository.save(user);

        AuthRequest request = new AuthRequest("testUser", "abhaysingh");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        AuthRequest request = new AuthRequest("wrongUser", "wrongPass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }
}

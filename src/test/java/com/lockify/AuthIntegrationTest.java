package com.lockify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lockify.phase1.coreauth.dto.LoginRequest;
import com.lockify.phase1.coreauth.dto.RegisterRequest;
import com.lockify.phase1.coreauth.dto.AuthResponse;
import com.lockify.phase1.coreauth.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Tests - poora auth flow end-to-end test karta hai.
 * Testcontainers se real PostgreSQL use hota hai - production jaisa environment.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("lockify_test")
            .withUsername("lockify")
            .withPassword("lockify_secret");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.autoconfigure.exclude", () ->
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Full flow: Register -> Login -> Access protected API")
    void fullAuthFlow() throws Exception {
        String unique = String.valueOf(System.currentTimeMillis());
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("user_" + unique)
                .email("user_" + unique + "@test.com")
                .password("Password@123")
                .build();

        // Step 1: Register
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(registerRequest.getUsername()))
                .andReturn();

        UserResponse registered = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), UserResponse.class);
        assertThat(registered.getRoles()).contains("USER");

        // Step 2: Login
        LoginRequest loginRequest = LoginRequest.builder()
                .identifier(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        AuthResponse auth = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class);

        // Step 3: Protected endpoint with JWT
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + auth.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(registerRequest.getEmail()));

        // Step 4: Admin endpoint - USER role se forbidden hona chahiye
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + auth.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Invalid registration - weak password reject hona chahiye")
    void invalidPassword() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("weakuser")
                .email("weak@test.com")
                .password("weak")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.password").exists());
    }

    @Test
    @DisplayName("Bina token ke protected API - 401 aana chahiye")
    void unauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}

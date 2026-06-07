package com.kata.customers.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest(
    properties = {
        "app.jwt.secret=0123456789abcdef0123456789abcdef",
        "app.jwt.expiration-ms=3600000"
    }
)
class AuthFlowIntegrationTest {

    private record TokenPair(String accessToken, String refreshToken) {}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void logoutShouldRevokeJwtAndBlockFutureRequests() throws Exception {
        String username = "user_logout_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String registerPayload =
            """
            {
              "username": "%s",
              "email": "%s@mail.com",
              "password": "Secret123*"
            }
            """.formatted(username, username);

        MvcResult registerResult = mockMvc
            .perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerPayload)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty())
            .andReturn();

        TokenPair tokenPair = extractTokens(registerResult);
        String token = tokenPair.accessToken();

        mockMvc
            .perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username));

        mockMvc
            .perform(post("/api/auth/logout").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Sesion cerrada correctamente"));

        mockMvc
            .perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());

        mockMvc
            .perform(
                post("/api/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"refreshToken\":\"" + tokenPair.refreshToken() + "\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    private TokenPair extractTokens(MvcResult result) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return new TokenPair(root.path("token").asText(), root.path("refreshToken").asText());
    }
}

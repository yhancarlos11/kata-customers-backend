package com.kata.customers.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.customers.common.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        objectMapper = new ObjectMapper();
        AuthController controller = new AuthController(authService);
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    void registerShouldReturnTokenPair() throws Exception {
        AuthResponse response = new AuthResponse("access-token", "refresh-token");
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        String payload =
            """
            {
              "username": "demoUser",
              "email": "demo@correo.com",
              "password": "Secret123*"
            }
            """;

        mockMvc
            .perform(post("/api/auth/register").contentType("application/json").content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void refreshShouldReturnNewTokens() throws Exception {
        when(authService.refresh("refresh-old"))
            .thenReturn(new AuthResponse("access-new", "refresh-new"));

        String payload = "{" + "\"refreshToken\":\"refresh-old\"" + "}";

        mockMvc
            .perform(post("/api/auth/refresh").contentType("application/json").content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("access-new"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-new"));

        verify(authService).refresh("refresh-old");
    }

    @Test
    void refreshShouldFailWhenRefreshTokenIsBlank() throws Exception {
        String payload = "{" + "\"refreshToken\":\"\"" + "}";

        mockMvc
            .perform(post("/api/auth/refresh").contentType("application/json").content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors.refreshToken").exists());
    }

    @Test
    void logoutShouldPassAccessAndRefreshTokens() throws Exception {
        when(authService.logout(eq("access-token"), eq("refresh-token")))
            .thenReturn(new LogoutResponse("Sesion cerrada correctamente"));

        LogoutRequest request = new LogoutRequest();
        request.setRefreshToken("refresh-token");

        mockMvc
            .perform(
                post("/api/auth/logout")
                    .header("Authorization", "Bearer access-token")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Sesion cerrada correctamente"));

        verify(authService).logout("access-token", "refresh-token");
    }

    @Test
    void logoutShouldReturnBadRequestForInvalidAuthorizationHeader() throws Exception {
        mockMvc
            .perform(post("/api/auth/logout").header("Authorization", "invalid-header"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Encabezado Authorization invalido"));
    }

    @Test
    void meShouldReturnAuthenticatedUserData() throws Exception {
        when(authService.me("demoUser"))
            .thenReturn(new AuthMeResponse("demoUser", "demo@correo.com", "USER"));

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("demoUser", "N/A");

        mockMvc
            .perform(get("/api/auth/me").principal(authentication))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("demoUser"))
            .andExpect(jsonPath("$.email").value("demo@correo.com"))
            .andExpect(jsonPath("$.role").value("USER"));

        verify(authService).me("demoUser");
    }
}

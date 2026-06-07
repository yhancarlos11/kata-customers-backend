package com.kata.customers.common;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class EnvironmentControllerTest {

    @Mock
    private Environment environment;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        EnvironmentController controller = new EnvironmentController(environment);
        ReflectionTestUtils.setField(controller, "appName", "customers-dev");
        ReflectionTestUtils.setField(controller, "serverPort", "8080");
        ReflectionTestUtils.setField(controller, "environmentMessage", "Ejecutando en DEV");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void environmentInfoShouldReturnActiveProfileAndMetadata() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[] { "dev" });

        mockMvc
            .perform(get("/api/info/environment"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.application").value("customers-dev"))
            .andExpect(jsonPath("$.port").value("8080"))
            .andExpect(jsonPath("$.message").value("Ejecutando en DEV"))
            .andExpect(jsonPath("$.activeProfile").value("dev"));
    }

    @Test
    void healthShouldReturnUpStatus() throws Exception {
        mockMvc
            .perform(get("/api/info/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}

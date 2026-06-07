package com.kata.customers.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kata.customers.common.GlobalExceptionHandler;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ProductController controller = new ProductController(productService);
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    void createShouldReturnCreatedProduct() throws Exception {
        when(productService.create(any(Long.class), any(CreateProductRequest.class)))
            .thenReturn(new ProductResponse(10L, "Laptop", new BigDecimal("2500000"), "Equipo"));

        String payload =
            """
            {
              "name": "Laptop",
              "price": 2500000,
              "description": "Equipo"
            }
            """;

        mockMvc
            .perform(
                post("/api/customers/1/products")
                    .contentType("application/json")
                    .content(payload)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.name").value("Laptop"))
            .andExpect(jsonPath("$.price").value(2500000));
    }

    @Test
    void createShouldFailValidationWhenNameIsBlankAndPriceIsInvalid() throws Exception {
        String payload =
            """
            {
              "name": "",
              "price": 0,
              "description": ""
            }
            """;

        mockMvc
            .perform(
                post("/api/customers/1/products")
                    .contentType("application/json")
                    .content(payload)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors.name").exists())
            .andExpect(jsonPath("$.validationErrors.price").exists());
    }

    @Test
    void listByCustomerShouldReturnProducts() throws Exception {
        when(productService.listByCustomer(1L))
            .thenReturn(
                List.of(
                    new ProductResponse(1L, "Mouse", new BigDecimal("50000"), "Inalambrico"),
                    new ProductResponse(2L, "Teclado", new BigDecimal("120000"), null)
                )
            );

        mockMvc
            .perform(get("/api/customers/1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Mouse"))
            .andExpect(jsonPath("$[1].name").value("Teclado"));

        verify(productService).listByCustomer(1L);
    }

    @Test
    void updateShouldReturnUpdatedProduct() throws Exception {
        when(productService.update(any(Long.class), any(Long.class), any(CreateProductRequest.class)))
            .thenReturn(new ProductResponse(5L, "Monitor", new BigDecimal("900000"), "27 pulgadas"));

        String payload =
            """
            {
              "name": "Monitor",
              "price": 900000,
              "description": "27 pulgadas"
            }
            """;

        mockMvc
            .perform(
                put("/api/customers/1/products/5")
                    .contentType("application/json")
                    .content(payload)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.name").value("Monitor"));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/customers/1/products/9")).andExpect(status().isNoContent());

        verify(productService).delete(1L, 9L);
    }
}

package com.kata.customers.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kata.customers.infrastructure.persistence.repository.ProductRepository;
import com.kata.customers.product.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductJpaAdapterTest {

    @Mock
    private ProductRepository productRepository;

    @Test
    void shouldDelegateProductOperations() {
        ProductJpaAdapter adapter = new ProductJpaAdapter(productRepository);
        Product product = new Product();
        product.setId(9L);
        product.setName("Mouse");
        product.setPrice(new BigDecimal("50000"));

        when(productRepository.save(product)).thenReturn(product);
        when(productRepository.findByCustomerId(1L)).thenReturn(List.of(product));
        when(productRepository.findByIdAndCustomerId(9L, 1L)).thenReturn(Optional.of(product));

        assertSame(product, adapter.save(product));
        assertEquals(1, adapter.findByCustomerId(1L).size());
        assertTrue(adapter.findByIdAndCustomerId(9L, 1L).isPresent());

        adapter.delete(product);

        verify(productRepository).save(product);
        verify(productRepository).findByCustomerId(1L);
        verify(productRepository).findByIdAndCustomerId(9L, 1L);
        verify(productRepository).delete(product);
    }
}

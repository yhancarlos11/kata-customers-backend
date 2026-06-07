package com.kata.customers.product;

import com.kata.customers.common.ResourceNotFoundException;
import com.kata.customers.customer.Customer;
import com.kata.customers.customer.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public ProductService(ProductRepository productRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public ProductResponse create(Long customerId, CreateProductRequest request) {
        Customer customer = findCustomerOrThrow(customerId);

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setCustomer(customer);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public List<ProductResponse> listByCustomer(Long customerId) {
        findCustomerOrThrow(customerId);
        return productRepository.findByCustomerId(customerId).stream().map(this::toResponse).toList();
    }

    public ProductResponse update(Long customerId, Long productId, CreateProductRequest request) {
        Product product = productRepository
            .findByIdAndCustomerId(productId, customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para este cliente"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public void delete(Long customerId, Long productId) {
        Product product = productRepository
            .findByIdAndCustomerId(productId, customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para este cliente"));

        productRepository.delete(product);
    }

    private Customer findCustomerOrThrow(Long customerId) {
        return customerRepository
            .findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getDescription()
        );
    }
}

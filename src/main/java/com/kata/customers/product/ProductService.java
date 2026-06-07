package com.kata.customers.product;

import com.kata.customers.application.port.in.ProductUseCase;
import com.kata.customers.application.port.out.CustomerPort;
import com.kata.customers.application.port.out.ProductPort;
import com.kata.customers.common.ResourceNotFoundException;
import com.kata.customers.customer.Customer;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements ProductUseCase {

    private final ProductPort productPort;
    private final CustomerPort customerPort;

    public ProductService(ProductPort productPort, CustomerPort customerPort) {
        this.productPort = productPort;
        this.customerPort = customerPort;
    }

    @Override
    public ProductResponse create(Long customerId, CreateProductRequest request) {
        Customer customer = findCustomerOrThrow(customerId);

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setCustomer(customer);

        Product saved = productPort.save(product);
        return toResponse(saved);
    }

    @Override
    public List<ProductResponse> listByCustomer(Long customerId) {
        findCustomerOrThrow(customerId);
        return productPort.findByCustomerId(customerId).stream().map(this::toResponse).toList();
    }

    @Override
    public ProductResponse update(Long customerId, Long productId, CreateProductRequest request) {
        Product product = productPort
            .findByIdAndCustomerId(productId, customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para este cliente"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());

        Product saved = productPort.save(product);
        return toResponse(saved);
    }

    @Override
    public void delete(Long customerId, Long productId) {
        Product product = productPort
            .findByIdAndCustomerId(productId, customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para este cliente"));

        productPort.delete(product);
    }

    private Customer findCustomerOrThrow(Long customerId) {
        return customerPort
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

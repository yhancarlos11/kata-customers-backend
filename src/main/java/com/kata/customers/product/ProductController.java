package com.kata.customers.product;

import com.kata.customers.application.port.in.ProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers/{customerId}/products")
@Tag(name = "Productos", description = "CRUD de productos por cliente")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @PostMapping
    @Operation(
        summary = "Crear producto de un cliente",
        description = "Crea un producto asociado al cliente identificado por customerId"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ProductResponse> create(
        @PathVariable("customerId") Long customerId,
        @Valid @RequestBody CreateProductRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productUseCase.create(customerId, request));
    }

    @GetMapping
    @Operation(
        summary = "Listar productos de un cliente",
        description = "Retorna todos los productos asociados al cliente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de productos obtenido"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<List<ProductResponse>> listByCustomer(
        @PathVariable("customerId") Long customerId
    ) {
        return ResponseEntity.ok(productUseCase.listByCustomer(customerId));
    }

    @PutMapping("/{productId}")
    @Operation(
        summary = "Actualizar producto de un cliente",
        description = "Actualiza nombre, precio y descripcion de un producto asociado al cliente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado")
    })
    public ResponseEntity<ProductResponse> update(
        @PathVariable("customerId") Long customerId,
        @PathVariable("productId") Long productId,
        @Valid @RequestBody CreateProductRequest request
    ) {
        return ResponseEntity.ok(productUseCase.update(customerId, productId, request));
    }

    @DeleteMapping("/{productId}")
    @Operation(
        summary = "Eliminar producto de un cliente",
        description = "Elimina un producto asociado al cliente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado")
    })
    public ResponseEntity<Void> delete(
        @PathVariable("customerId") Long customerId,
        @PathVariable("productId") Long productId
    ) {
        productUseCase.delete(customerId, productId);
        return ResponseEntity.noContent().build();
    }
}

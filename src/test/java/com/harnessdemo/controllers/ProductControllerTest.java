package com.harnessdemo.controllers;

import com.harnessdemo.models.Product;
import com.harnessdemo.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("29.99"));
        testProduct.setStock(100);
        testProduct.setCategory("Electronics");
        testProduct.setActive(true);
    }

    // ==================== Create Product Tests (4 tests) ====================

    @Test
    void testCreateProductSuccess() {
        when(productService.createProduct(anyString(), anyString(), any(BigDecimal.class),
            anyInt(), anyString())).thenReturn(testProduct);

        ResponseEntity<Product> response = productController.createProduct(
            new ProductController.CreateProductRequest("Test", "Desc",
                new BigDecimal("29.99"), 100, "Electronics"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateProductReturnsProduct() {
        when(productService.createProduct(anyString(), anyString(), any(BigDecimal.class),
            anyInt(), anyString())).thenReturn(testProduct);

        ResponseEntity<Product> response = productController.createProduct(
            new ProductController.CreateProductRequest("Test", "Desc",
                new BigDecimal("29.99"), 100, "Electronics"));

        assertNotNull(response.getBody());
    }

    @Test
    void testCreateProductCallsService() {
        when(productService.createProduct(anyString(), anyString(), any(BigDecimal.class),
            anyInt(), anyString())).thenReturn(testProduct);

        productController.createProduct(
            new ProductController.CreateProductRequest("Name", "Desc",
                new BigDecimal("10.00"), 50, "Category"));

        verify(productService).createProduct("Name", "Desc", new BigDecimal("10.00"), 50, "Category");
    }

    @Test
    void testCreateProductWithAllFields() {
        when(productService.createProduct(anyString(), anyString(), any(BigDecimal.class),
            anyInt(), anyString())).thenReturn(testProduct);

        ResponseEntity<Product> response = productController.createProduct(
            new ProductController.CreateProductRequest("Test Product", "Test Description",
                new BigDecimal("29.99"), 100, "Electronics"));

        assertEquals("Test Product", response.getBody().getName());
    }

    // ==================== Get Product Tests (4 tests) ====================

    @Test
    void testGetProductByIdSuccess() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));

        ResponseEntity<Product> response = productController.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productController.getProductById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllProducts() {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(testProduct));

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetActiveProducts() {
        when(productService.getActiveProducts()).thenReturn(Arrays.asList(testProduct));

        ResponseEntity<List<Product>> response = productController.getActiveProducts();

        assertEquals(1, response.getBody().size());
    }

    // ==================== Search and Filter Tests (4 tests) ====================

    @Test
    void testGetProductsByCategory() {
        when(productService.getProductsByCategory("Electronics")).thenReturn(Arrays.asList(testProduct));

        ResponseEntity<List<Product>> response = productController.getProductsByCategory("Electronics");

        assertEquals(1, response.getBody().size());
    }

    @Test
    void testSearchProducts() {
        when(productService.searchProducts("Test")).thenReturn(Arrays.asList(testProduct));

        ResponseEntity<List<Product>> response = productController.searchProducts("Test");

        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetProductsByPriceRange() {
        when(productService.getProductsByPriceRange(new BigDecimal("10.00"),
            new BigDecimal("50.00"))).thenReturn(Arrays.asList(testProduct));

        ResponseEntity<List<Product>> response = productController.getProductsByPriceRange(
            new BigDecimal("10.00"), new BigDecimal("50.00"));

        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetLowStockProducts() {
        when(productService.getLowStockProducts(10)).thenReturn(Arrays.asList(testProduct));

        ResponseEntity<List<Product>> response = productController.getLowStockProducts(10);

        assertEquals(1, response.getBody().size());
    }

    // ==================== Update and Delete Tests (4 tests) ====================

    @Test
    void testUpdateProductSuccess() {
        when(productService.updateProduct(anyLong(), anyString(), anyString(),
            any(BigDecimal.class), anyString())).thenReturn(testProduct);

        ResponseEntity<Product> response = productController.updateProduct(1L,
            new ProductController.UpdateProductRequest("Updated", "Desc",
                new BigDecimal("39.99"), "Category"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateStockSuccess() {
        testProduct.setStock(150);
        when(productService.updateStock(1L, 50)).thenReturn(testProduct);

        ResponseEntity<Product> response = productController.updateStock(1L,
            new ProductController.UpdateStockRequest(50));

        assertEquals(150, response.getBody().getStock());
    }

    @Test
    void testDeactivateProductSuccess() {
        testProduct.setActive(false);
        when(productService.deactivateProduct(1L)).thenReturn(testProduct);

        ResponseEntity<Product> response = productController.deactivateProduct(1L);

        assertFalse(response.getBody().isActive());
    }

    @Test
    void testDeleteProductSuccess() {
        doNothing().when(productService).deleteProduct(1L);

        ResponseEntity<Void> response = productController.deleteProduct(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // ==================== Additional Tests (4 tests) ====================

    @Test
    void testGetCategories() {
        when(productService.getActiveCategories()).thenReturn(Arrays.asList("Electronics", "Clothing"));

        ResponseEntity<List<String>> response = productController.getCategories();

        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllProductsEmpty() {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testSearchProductsEmpty() {
        when(productService.searchProducts("NonExistent")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Product>> response = productController.searchProducts("NonExistent");

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testDeleteProductCallsService() {
        doNothing().when(productService).deleteProduct(1L);

        productController.deleteProduct(1L);

        verify(productService).deleteProduct(1L);
    }
}

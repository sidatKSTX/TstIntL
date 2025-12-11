package com.harnessdemo.services;

import com.harnessdemo.models.Product;
import com.harnessdemo.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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

    // ==================== Create Product Tests (15 tests) ====================

    @Test
    void testCreateProductSuccess() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.createProduct("Test Product", "Description",
            new BigDecimal("29.99"), 100, "Electronics");

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testCreateProductWithNullPrice() {
        assertThrows(IllegalArgumentException.class, () ->
            productService.createProduct("Test", "Desc", null, 10, "Category"));
    }

    @Test
    void testCreateProductWithZeroPrice() {
        assertThrows(IllegalArgumentException.class, () ->
            productService.createProduct("Test", "Desc", BigDecimal.ZERO, 10, "Category"));
    }

    @Test
    void testCreateProductWithNegativePrice() {
        assertThrows(IllegalArgumentException.class, () ->
            productService.createProduct("Test", "Desc", new BigDecimal("-10.00"), 10, "Category"));
    }

    @Test
    void testCreateProductSetsActiveTrue() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Test", "Desc",
            new BigDecimal("10.00"), 10, "Category");

        assertTrue(result.isActive());
    }

    @Test
    void testCreateProductWithNullStock() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Test", "Desc",
            new BigDecimal("10.00"), null, "Category");

        assertEquals(0, result.getStock());
    }

    @Test
    void testCreateProductWithZeroStock() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Test", "Desc",
            new BigDecimal("10.00"), 0, "Category");

        assertEquals(0, result.getStock());
    }

    @Test
    void testCreateProductStoresName() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Specific Name", "Desc",
            new BigDecimal("10.00"), 10, "Category");

        assertEquals("Specific Name", result.getName());
    }

    @Test
    void testCreateProductStoresDescription() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Name", "Specific Description",
            new BigDecimal("10.00"), 10, "Category");

        assertEquals("Specific Description", result.getDescription());
    }

    @Test
    void testCreateProductStoresCategory() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Name", "Desc",
            new BigDecimal("10.00"), 10, "Specific Category");

        assertEquals("Specific Category", result.getCategory());
    }

    @Test
    void testCreateProductWithLargeStock() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Name", "Desc",
            new BigDecimal("10.00"), 999999, "Category");

        assertEquals(999999, result.getStock());
    }

    @Test
    void testCreateProductWithLargePrice() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Name", "Desc",
            new BigDecimal("99999.99"), 10, "Category");

        assertEquals(new BigDecimal("99999.99"), result.getPrice());
    }

    @Test
    void testCreateProductWithSmallPrice() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Name", "Desc",
            new BigDecimal("0.01"), 10, "Category");

        assertEquals(new BigDecimal("0.01"), result.getPrice());
    }

    @Test
    void testCreateProductRepositorySaveCalled() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.createProduct("Test", "Desc", new BigDecimal("10.00"), 10, "Category");

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProductWithSpecialCharacters() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct("Product's Name & More", "Desc",
            new BigDecimal("10.00"), 10, "Category");

        assertEquals("Product's Name & More", result.getName());
    }

    // ==================== Get Product Tests (15 tests) ====================

    @Test
    void testGetProductByIdSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllProducts() {
        Product product2 = new Product();
        product2.setId(2L);
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, product2));

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllProductsEmpty() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> result = productService.getAllProducts();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetActiveProducts() {
        when(productRepository.findByActive(true)).thenReturn(Arrays.asList(testProduct));

        List<Product> result = productService.getActiveProducts();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void testGetProductsByCategory() {
        when(productRepository.findByCategory("Electronics")).thenReturn(Arrays.asList(testProduct));

        List<Product> result = productService.getProductsByCategory("Electronics");

        assertEquals(1, result.size());
    }

    @Test
    void testGetProductsByCategoryEmpty() {
        when(productRepository.findByCategory("NonExistent")).thenReturn(Collections.emptyList());

        List<Product> result = productService.getProductsByCategory("NonExistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchProducts() {
        when(productRepository.findByNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(testProduct));

        List<Product> result = productService.searchProducts("Test");

        assertEquals(1, result.size());
    }

    @Test
    void testSearchProductsNoMatch() {
        when(productRepository.findByNameContainingIgnoreCase("NoMatch")).thenReturn(Collections.emptyList());

        List<Product> result = productService.searchProducts("NoMatch");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductsByPriceRange() {
        when(productRepository.findByPriceBetween(new BigDecimal("10.00"), new BigDecimal("50.00")))
            .thenReturn(Arrays.asList(testProduct));

        List<Product> result = productService.getProductsByPriceRange(
            new BigDecimal("10.00"), new BigDecimal("50.00"));

        assertEquals(1, result.size());
    }

    @Test
    void testGetLowStockProducts() {
        testProduct.setStock(5);
        when(productRepository.findLowStockProducts(10)).thenReturn(Arrays.asList(testProduct));

        List<Product> result = productService.getLowStockProducts(10);

        assertEquals(1, result.size());
    }

    @Test
    void testGetActiveCategories() {
        when(productRepository.findActiveCategories()).thenReturn(Arrays.asList("Electronics", "Clothing"));

        List<String> result = productService.getActiveCategories();

        assertEquals(2, result.size());
    }

    @Test
    void testGetActiveCategoriesEmpty() {
        when(productRepository.findActiveCategories()).thenReturn(Collections.emptyList());

        List<String> result = productService.getActiveCategories();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductReturnsAllFields() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        Product product = result.get();
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals(new BigDecimal("29.99"), product.getPrice());
        assertEquals(100, product.getStock());
        assertEquals("Electronics", product.getCategory());
    }

    @Test
    void testGetProductByIdCalledOnce() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.getProductById(1L);

        verify(productRepository, times(1)).findById(1L);
    }

    // ==================== Update Product Tests (15 tests) ====================

    @Test
    void testUpdateProductSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(1L, "Updated Name", "Updated Desc",
            new BigDecimal("39.99"), "Updated Category");

        assertEquals("Updated Name", result.getName());
    }

    @Test
    void testUpdateProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            productService.updateProduct(999L, "Name", "Desc", new BigDecimal("10.00"), "Category"));
    }

    @Test
    void testUpdateProductNameOnly() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(1L, "New Name", "Test Description",
            new BigDecimal("29.99"), "Electronics");

        assertEquals("New Name", result.getName());
    }

    @Test
    void testUpdateProductPreservesId() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(1L, "Name", "Desc",
            new BigDecimal("10.00"), "Category");

        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateProductPreservesStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(1L, "Name", "Desc",
            new BigDecimal("10.00"), "Category");

        assertEquals(100, result.getStock());
    }

    @Test
    void testUpdateStockIncrease() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateStock(1L, 50);

        assertEquals(150, result.getStock());
    }

    @Test
    void testUpdateStockDecrease() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateStock(1L, -30);

        assertEquals(70, result.getStock());
    }

    @Test
    void testUpdateStockToZero() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateStock(1L, -100);

        assertEquals(0, result.getStock());
    }

    @Test
    void testUpdateStockInsufficientStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(IllegalStateException.class, () -> productService.updateStock(1L, -150));
    }

    @Test
    void testUpdateStockNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateStock(999L, 10));
    }

    @Test
    void testDeactivateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.deactivateProduct(1L);

        assertFalse(result.isActive());
    }

    @Test
    void testDeactivateProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.deactivateProduct(999L));
    }

    @Test
    void testDeleteProductSuccess() {
        when(productRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> productService.deleteProduct(999L));
    }

    @Test
    void testUpdateProductSaveCalled() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.updateProduct(1L, "Name", "Desc", new BigDecimal("10.00"), "Category");

        verify(productRepository).save(any(Product.class));
    }
}

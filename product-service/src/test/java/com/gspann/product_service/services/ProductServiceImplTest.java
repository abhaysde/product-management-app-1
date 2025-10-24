package com.gspann.product_service.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.gspann.product_service.entity.Product;
import com.gspann.product_service.repository.ProductRepository;
import com.gspann.product_service.service.impl.ProductServiceImpl;
import com.gspann.product_service.service.impl.S3Service;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Product");
        sampleProduct.setPrice(100.0);
        sampleProduct.setDiscountPrice(80.0);
        sampleProduct.setAvailable(true);
        sampleProduct.setDeleted(false);
    }

    @Test
    void testCreateProductWithImage_NewProduct_WithImage() throws Exception {
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);
        when(s3Service.uploadFile(mockImage)).thenReturn("https://s3.aws/test.jpg");
        when(productRepository.findByname("Test Product")).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product result = productService.createProductWithImage(sampleProduct, mockImage);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(s3Service, times(1)).uploadFile(mockImage);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProductWithImage_ExistingProduct_UpdatesIt() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Test Product");
        existingProduct.setPrice(50.0);

        when(productRepository.findByname("Test Product")).thenReturn(existingProduct);
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        Product result = productService.createProductWithImage(sampleProduct, null);

        assertEquals(100.0, result.getPrice());
        assertTrue(result.isAvailable());
        assertFalse(result.isDeleted());
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testCreateProductWithImage_Exception() throws Exception {
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);
        when(s3Service.uploadFile(mockImage)).thenThrow(new RuntimeException("S3 error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                productService.createProductWithImage(sampleProduct, mockImage)
        );

        assertTrue(thrown.getMessage().contains("Error uploading product image"));
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindProductById_Found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.findProductById(1L);

        assertEquals("Test Product", result.getName());
    }

    @Test
    void testFindProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                productService.findProductById(1L));

        assertEquals("Product not found with id: 1", ex.getMessage());
    }

    @Test
    void testUpdateProduct_KeepsExistingImageUrl() {
        Product oldProduct = new Product();
        oldProduct.setId(1L);
        oldProduct.setImageUrl("old-image-url");

        when(productRepository.findById(1L)).thenReturn(Optional.of(oldProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product update = new Product();
        update.setName("Updated Product");
        update.setImageUrl(null); // should retain old one

        Product result = productService.updateProduct(1L, update);

        assertEquals("old-image-url", result.getImageUrl());
        assertEquals("Updated Product", result.getName());
    }

    @Test
    void testDeleteProduct_SoftDelete() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        productService.deleteProduct(1L);

        assertTrue(sampleProduct.isDeleted());
        assertFalse(sampleProduct.isAvailable());
        assertNotNull(sampleProduct.getDeletedDate());
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void testGetAllProductsByName() {
        when(productRepository.findBynameContainingIgnoreCase("test")).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getAllProductsByName("test");

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productRepository, times(1)).findBynameContainingIgnoreCase("test");
    }
}

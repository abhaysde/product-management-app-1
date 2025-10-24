package com.gspann.product_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gspann.product_service.entity.Product;
import com.gspann.product_service.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService productService;

	@Autowired
	private ObjectMapper objectMapper;

	private Product sampleProduct;

	@BeforeEach
	void setup() {
		sampleProduct = new Product();
		sampleProduct.setId(1L);
		sampleProduct.setName("Test Product");
		sampleProduct.setDescription("Test Description");
		sampleProduct.setPrice(100.0);
	}

	@Test
	void testCreateProduct_withImage() throws Exception {
		MockMultipartFile image = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
				"dummy image data".getBytes());

		MockMultipartFile productPart = new MockMultipartFile("product", "", MediaType.APPLICATION_JSON_VALUE,
				objectMapper.writeValueAsBytes(sampleProduct));

		when(productService.createProductWithImage(any(Product.class), any())).thenReturn(sampleProduct);

		mockMvc.perform(
				multipart("/api/product").file(productPart).file(image).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Test Product"));
	}

	@Test
	void testCreateProduct_withoutImage() throws Exception {
		MockMultipartFile productPart = new MockMultipartFile("product", "", MediaType.APPLICATION_JSON_VALUE,
				objectMapper.writeValueAsBytes(sampleProduct));

		when(productService.createProductWithImage(any(Product.class), any())).thenReturn(sampleProduct);

		mockMvc.perform(multipart("/api/product").file(productPart).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Test Product"));
	}

	@Test
	void testGetAllProducts() throws Exception {
		List<Product> products = Arrays.asList(sampleProduct);
		when(productService.getAllProducts()).thenReturn(products);

		mockMvc.perform(get("/api")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Test Product"));
	}

	@Test
	void testGetProductById_found() throws Exception {
		when(productService.findProductById(1L)).thenReturn(sampleProduct);

		mockMvc.perform(get("/api/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Test Product"));
	}

	@Test
	void testGetProductById_notFound() throws Exception {
		when(productService.findProductById(2L)).thenThrow(new RuntimeException("Product not found"));

		mockMvc.perform(get("/api/2")).andExpect(status().isNotFound());
	}

	@Test
	void testUpdateProduct() throws Exception {
		Product updatedProduct = new Product();
		updatedProduct.setId(1L);
		updatedProduct.setName("Updated Product");
		updatedProduct.setDescription("Updated Description");
		updatedProduct.setPrice(200.0);

		when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

		mockMvc.perform(put("/api/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedProduct))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Product"));
	}

	@Test
	void testDeleteProduct() throws Exception {
		doNothing().when(productService).deleteProduct(1L);

		mockMvc.perform(delete("/api/1")).andExpect(status().isNoContent());
	}
}

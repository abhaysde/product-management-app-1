package com.gspann.product_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gspann.product_service.entity.Product;
import com.gspann.product_service.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired
	private ProductService productService;

	@PostMapping(value = "/product", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") Product product,
    	@RequestPart(value = "image", required = false) MultipartFile image) {

        Product savedProduct = productService.createProductWithImage(product, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProducts() {
		return ResponseEntity.ok(productService.getAllProducts());
	}

	@GetMapping("/product/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		try {
			Product product = productService.findProductById(id);
			return ResponseEntity.ok(product);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build(); // remove try catch later, make a global exception handler
		}

	}

	@PutMapping("/product/{id}")
	public ResponseEntity<Product> updateProductById(@PathVariable Long id, @RequestBody Product product) {
		Product updated = productService.updateProduct(id, product);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/product/{id}")
	public ResponseEntity<Product> deleteProductById(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
}

package com.gspann.product_service.service;

import com.gspann.product_service.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
	Product createProductWithImage(Product product, MultipartFile image);

	List<Product> getAllProducts();

	List<Product> getAllProductsByName(String name);

	Product findProductById(Long id);

	Product updateProduct(Long id, Product product);

	void deleteProduct(Long id);
}

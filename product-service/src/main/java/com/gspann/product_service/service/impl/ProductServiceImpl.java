package com.gspann.product_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gspann.product_service.entity.Product;
import com.gspann.product_service.repository.ProductRepository;
import com.gspann.product_service.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private S3Service s3Service;

	@Override
	public Product createProductWithImage(Product product, MultipartFile image) {
		try {
			if (image != null && !image.isEmpty()) {
				String imageUrl = s3Service.uploadFile(image);
				product.setImageUrl(imageUrl);
			}

			Product byPName = productRepository.findByname(product.getName());

			if (byPName == null) {
				return productRepository.save(product);
			} else {
				byPName.setPrice(product.getPrice());
				byPName.setDiscountPrice(product.getDiscountPrice());
				byPName.setAvailable(true);
				byPName.setDeleted(false);
				byPName.setDeletedDate(null);
				return productRepository.save(byPName);

			}

		} catch (Exception e) {
			throw new RuntimeException("Error uploading product image: " + e.getMessage());
		}
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Product findProductById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
	}

	@Override
	public Product updateProduct(Long id, Product product) {

		if (product.getImageUrl() == null) {
			product.setImageUrl(this.productRepository.findById(id).get().getImageUrl());
		}
		product.setId(id);
		return productRepository.save(product);
	}

	@Override
	public void deleteProduct(Long id) {
		Product old = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

		old.setDeleted(true);
		old.setDeletedDate(LocalDateTime.now());
		old.setAvailable(false);

		productRepository.save(old); // update instead of delete
	}

	@Override
	public List<Product> getAllProductsByName(String query) {
		return productRepository.findBynameContainingIgnoreCase(query);
	}
}

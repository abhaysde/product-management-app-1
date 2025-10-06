package com.gspann.product_service.service.impl;

import com.gspann.product_service.entity.Product;
import com.gspann.product_service.repository.ProductRepository;
import com.gspann.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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
                String imageUrl = s3Service.uploadFile(image); // upload to AWS S3
                product.setImageUrl(imageUrl);
            }
            return productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading product image: " + e.getMessage());
        }
    }


    @Override
    public List<Product> getAllProducts() {
        return productRepository.findByIsDeletedFalse();
    }

    @Override
    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found with id: "+id));
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product old=productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found with id: "+id));
        if (product.getName() != null) {
            old.setName(product.getName());
        }
        if (product.getQuantity() != null) {
            old.setQuantity(product.getQuantity());
        }
        if (product.getPrice() != null) {
            old.setPrice(product.getPrice());
        }
        if (product.getDiscountPrice() != null) {
            old.setDiscountPrice(product.getDiscountPrice());
        }
        if (product.getDeletedDate() != null) {
            old.setDeletedDate(product.getDeletedDate());
        }
        if (product.getIsAvailable() != null) {
            old.setIsAvailable(product.getIsAvailable());
        }

        return productRepository.save(old);
    }

    @Override
    public void deleteProduct(Long id) {
        Product old = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        old.setIsDeleted(true);
        old.setDeletedDate(LocalDateTime.now());

        productRepository.save(old); // update instead of delete
    }
}

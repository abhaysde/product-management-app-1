package com.gspann.product_service.repository;

import com.gspann.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByIsDeletedFalse();
}

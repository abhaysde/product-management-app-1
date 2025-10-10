package com.gspann.product_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gspann.product_service.entity.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByDeletedFalse();
   Product findByname(String pName); 
   List<Product> findBynameContainingIgnoreCase(String pName);

}

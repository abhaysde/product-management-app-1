package com.gspann.product_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gspann.product_service.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByDeletedFalse();

    Product findBypName(String pName);

    List<Product> findBypNameContainingIgnoreCase(String pName);
}

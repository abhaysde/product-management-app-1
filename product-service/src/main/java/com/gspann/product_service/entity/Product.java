package com.gspann.product_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private Double price;
    private Double discountPrice;
    private Boolean isAvailable;
    private Boolean isDeleted = false;

    private LocalDateTime deletedDate;

    private String imageUrl;

}

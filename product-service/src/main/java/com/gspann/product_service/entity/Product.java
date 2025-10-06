package com.gspann.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

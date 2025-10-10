package com.gspann.product_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String imageUrl;
	@Column(nullable = false, unique = true)
	private String name;
	@Column(nullable = false)
	private int quantity;
	@Column(nullable = false)
	private double price;
	private Double discountPrice;
	private boolean isAvailable;
	private Boolean deleted = false;
	private LocalDateTime deletedDate;
}

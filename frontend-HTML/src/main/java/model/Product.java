package model;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class Product {
	private Long id;

	private String imageUrl;	
	private String pName;
	private Integer pQuantity;
	private Double pPrice;
	private Double pDiscount;
	private Boolean available;
	private Boolean deleted = false;
	private LocalDateTime deletedDate;
}

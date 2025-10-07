package model;

import lombok.Data;
@Data
public class Product {
	private int id;
	private String pName;
	private int pQuantity;
	private double pPrice;
	private double pDiscount;
	private Boolean available;
	private Boolean deleted;
	private String deletedDate;
	private String imageUrl;
}

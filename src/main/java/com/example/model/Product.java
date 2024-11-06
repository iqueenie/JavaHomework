package com.example.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {
	@Id
	private String productId;
	private String productName;
	private String shortName;
	private boolean isGroup;
	
	 @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	 private List<Prices> prices;

	 public Product() {
		 
	 }
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public boolean isGroup() {
		return isGroup;
	}
	public void setIsGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
	
	
}

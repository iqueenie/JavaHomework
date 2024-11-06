package com.example.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prices")
public class Prices {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer priceId;
	
	@ManyToOne
	@JoinColumn(name = "productId")
	private Product product;
	private Date priceDate;
	private Double price;
	
	
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Date getPriceDate() {
		return priceDate;
	}
	public void setPriceDate(Date date) {
		this.priceDate = date;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	

}

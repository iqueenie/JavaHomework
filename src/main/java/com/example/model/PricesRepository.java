package com.example.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PricesRepository extends JpaRepository<Prices, Integer> {
	
	
	 @Query("SELECT p FROM Prices p WHERE p.product.productId = :productId AND p.priceDate BETWEEN :startDate AND :endDate")
	    List<Prices> findByProductIdAndDateRange(@Param("productId") String productId, 
	                                              @Param("startDate") Date startDate, 
	                                              @Param("endDate") Date endDate);

	 Prices findByProductAndPriceDate(Product product, Date priceDate);
}

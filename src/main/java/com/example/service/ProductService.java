package com.example.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.model.Prices;
import com.example.model.PricesRepository;
import com.example.model.Product;
import com.example.model.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository pRepository;
	@Autowired
	private PricesRepository pricesRepository;
	
	
	
	public Prices getOneDayPrice(String productId, Date date) {
		Product product = pRepository.findById(productId).orElse(null); 
	    return pricesRepository.findByProductAndPriceDate(product, date);
	}
	
	public Prices changeOneDayPrice(String productId, Date date, double newPrices) {
		 Product product = pRepository.findById(productId).orElse(null); 
		 Prices price = pricesRepository.findByProductAndPriceDate(product, date);
		if(price!=null) {
			price.setPrice(newPrices);
			return pricesRepository.save(price);
		}
		return null;
	}
	
	public Prices insertPrices(Prices newPrice) {
		return pricesRepository.save(newPrice);
		
	}
	
	public boolean deletePrices(String productId, Date date) {
		 Product product = pRepository.findById(productId).orElse(null);
		 Prices price = pricesRepository.findByProductAndPriceDate(product, date);
	        if (price != null) {
	            pricesRepository.delete(price);
	            return true;
	        }
	        return false; 
	    }
	//呼叫api並將值插入表格
	@Transactional
	public void putJson() {
		
		 RestTemplate restTemplate = new RestTemplate();
		 
		 String path = "https://www.cathaybk.com.tw/cathaybk/service/newwealth/fund/chartservice.asmx/GetFundNavChart";
		 String bodyJson = "{\"req\":{\"Keys\":[\"10480016\"],\"From\":\"2023/03/10\",\"To\":\"2024/03/10\"}}";
		 HttpHeaders headers = new HttpHeaders();
		 headers.setContentType(MediaType.APPLICATION_JSON);

		 HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);
		 
		 try {
		 String response = restTemplate.postForObject(path, entity, String.class);
		 ObjectMapper mapper = new ObjectMapper();
		
			 JsonNode node = mapper.readTree(response);
			 JsonNode dataNode = node.path("Data");
	        
			 for(JsonNode item :dataNode) {
				 	String productId = item.path("id").asText(); 
	                String productName = item.path("name").asText(); 
	                String shortName = item.path("shortName").asText(); 
	                boolean isGrouped = item.path("dataGrouping").asBoolean(false);
	                
	                Product products = new Product();	            	
	                products.setProductId(productId);
	                products.setProductName(productName);
	                products.setShortName(shortName != null ? shortName : null);
	                products.setIsGroup(isGrouped);

	                pRepository.save(products);
	                JsonNode pricesArray = item.path("data");
	                for (JsonNode priceInfo : pricesArray) {
	                    long timestamp = priceInfo.get(0).asLong(); 
	                    double priceValue = priceInfo.get(1).asDouble(); 
	                    
	                    Prices prices = new Prices();
	                    prices.setProduct(products);
	                    prices.setPriceDate(new Date(timestamp)); 
	                    prices.setPrice(priceValue);
	                   
	                    pricesRepository.save(prices);
	                    System.out.println("Saved prices: " + prices);
	                }
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	//計算漲跌
	public double calculatePrice(String ProductId, Date startDate, Date endDate) {
		List<Prices> price = pricesRepository.findByProductIdAndDateRange(ProductId, startDate, endDate);
		 double startPrice = price.get(0).getPrice();
	     double endPrice = price.get(price.size() - 1).getPrice();
	     return endPrice - startPrice;
	}
	//計算漲跌幅
	public double calculatePricePersent(String ProductId, Date startDate, Date endDate) {
		List<Prices> price = pricesRepository.findByProductIdAndDateRange(ProductId, startDate, endDate);
		 double startPrice = price.get(0).getPrice();
	     double endPrice = price.get(price.size() - 1).getPrice();
	     if (price.size() < 2) {
	    	    throw new IllegalArgumentException("Not enough price data to calculate percentage change.");
	    	}

	     return (endPrice - startPrice)/startPrice;
	}
}

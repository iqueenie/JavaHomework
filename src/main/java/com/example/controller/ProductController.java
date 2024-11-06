package com.example.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.model.Prices;
import com.example.model.PricesRepository;
import com.example.model.Product;
import com.example.model.ProductRepository;
import com.example.service.ProductService;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService pService;
    @Autowired
	private ProductRepository pRepository;
    @Autowired
    private PricesRepository pricesRepository;

    //呼叫api並插入值到數據庫
    @PostMapping("/import")
    public ResponseEntity<String> importJson() {
        pService.putJson();
        return ResponseEntity.ok("Data imported successfully!");
    }
    //根據日期查找金額
    @GetMapping("/getPrice/{productId}/{priceDate}")
    public ResponseEntity<Double> getOneDayPrice(@PathVariable String productId, 
                                                  @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date priceDate) {
        Prices price = pService.getOneDayPrice(productId, priceDate);
        if (price != null) {
            return ResponseEntity.ok(price.getPrice());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //根據日期修改金額
    @PutMapping("/changePrice/{productId}/{priceDate}")
    public ResponseEntity<Double> changeOneDayPrice(@PathVariable String productId, 
                                                     @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date priceDate, 
                                                     @RequestParam double newPrice) {
        Prices updatedPrice = pService.changeOneDayPrice(productId, priceDate, newPrice);
        if (updatedPrice != null) {
            return ResponseEntity.ok(updatedPrice.getPrice());
        } else {
            return ResponseEntity.notFound().build(); 
        }
    }
    //添加金額
    @PostMapping("/addPrice")
    public ResponseEntity<Prices> addPrice(@RequestParam String productId, 
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date priceDate, 
                                            @RequestParam double price) {
        Prices newPrice = new Prices();
        Product product = pRepository.findById(productId).orElse(null); 

        if (product == null) {
            return ResponseEntity.badRequest().body(null); 
        }

        newPrice.setProduct(product);
        newPrice.setPriceDate(priceDate);
        newPrice.setPrice(price);
        
        Prices savedPrice = pricesRepository.save(newPrice);
        return ResponseEntity.ok(savedPrice);
    }


    //根據日期刪除金額
    @DeleteMapping("/deletePrice/{productId}/{priceDate}")
    public ResponseEntity<String> deletePrice(@PathVariable String productId, 
                                               @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date priceDate) {
        boolean isDeleted = pService.deletePrices(productId, priceDate);
        if (isDeleted) {
            return ResponseEntity.ok("Price deleted successfully!");
        } else {
            return ResponseEntity.notFound().build(); 
        }
    }
    //計算漲跌
    @GetMapping("/price/change")
    public ResponseEntity<Double> getPriceChange(@RequestParam String productId,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        double change = pService.calculatePrice(productId, startDate, endDate);
        return ResponseEntity.ok(change);
    }

    // 計算漲跌幅
    @GetMapping("/price/change-percentage")
    public ResponseEntity<Double> getPriceChangePercentage(@RequestParam String productId,
                                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        double changePercentage = pService.calculatePricePersent(productId, startDate, endDate);
        return ResponseEntity.ok(changePercentage);
    }

}


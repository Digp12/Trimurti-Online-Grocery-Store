package com.example.dto;

public class ProductSalesDTO {
    private String productName;
    private String category;
    private Long totalQuantity;
    private Double totalRevenue;

    public ProductSalesDTO() {
    }

    public ProductSalesDTO(String productName, String category, Long totalQuantity, Double totalRevenue) {
        this.productName = productName;
        this.category = category;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
} 
package com.example.dto;

import java.time.LocalDate;
import java.util.List;

public class SalesReportDTO {
    private String reportType; // DAILY, MONTHLY, QUARTERLY, YEARLY
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<DailySalesDTO> salesData;
    private Double totalRevenue;
    private Long totalOrders;
    private List<ProductSalesDTO> topSellingProducts;

    public SalesReportDTO() {
    }

    public SalesReportDTO(String reportType, LocalDate fromDate, LocalDate toDate, List<DailySalesDTO> salesData,
                         Double totalRevenue, Long totalOrders, List<ProductSalesDTO> topSellingProducts) {
        this.reportType = reportType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.salesData = salesData;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.topSellingProducts = topSellingProducts;
    }

    // Getters and Setters
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public List<DailySalesDTO> getSalesData() {
        return salesData;
    }

    public void setSalesData(List<DailySalesDTO> salesData) {
        this.salesData = salesData;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public List<ProductSalesDTO> getTopSellingProducts() {
        return topSellingProducts;
    }

    public void setTopSellingProducts(List<ProductSalesDTO> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }
} 
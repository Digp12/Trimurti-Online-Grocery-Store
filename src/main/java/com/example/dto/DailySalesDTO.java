package com.example.dto;

import java.time.LocalDate;

public class DailySalesDTO {
	private LocalDate date;
	private Long totalOrders;
	private Double totalRevenue;
	private Double totalRevenueForRange;

	public DailySalesDTO(LocalDate date, Long totalOrders, Double totalRevenue) {
		this.date = date;
		this.totalOrders = totalOrders;
		this.totalRevenue = totalRevenue;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(Long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public Double getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(Double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public Double getTotalRevenueForRange() {
		return totalRevenueForRange;
	}

	public void setTotalRevenueForRange(Double totalRevenueForRange) {
		this.totalRevenueForRange = totalRevenueForRange;
	}
}

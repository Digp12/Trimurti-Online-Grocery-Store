package com.example.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.example.dto.DailySalesDTO;
import com.example.dto.SalesReportDTO;
import com.example.model.Orders;

public interface OrderService {

	void saveOrder(Integer userId, Orders order) throws Exception;

	List<Orders> getOrdersByUser(Integer userId);

	Orders updateOrderStatus(Integer orderId, String status);

	List<Orders> getAllOrders();

	Orders getOrdersByOrderId(String orderId);

	Page<Orders> getAllOrdersPagination(Integer pageNo, Integer pageSize);

	long getOrderCount();

	Orders getOrdersById(Integer id);

	List<DailySalesDTO> getSalesReportBetween(LocalDate from, LocalDate to);

	public Double calculateTotalRevenue(LocalDate startDate, LocalDate endDate);

	Page<Orders> getOrdersByStatus(String status, int pageNo, int pageSize);

	// New methods for enhanced sales reporting
	SalesReportDTO getSalesReport(String reportType, LocalDate from, LocalDate to);

	List<DailySalesDTO> getMonthlySalesReport(LocalDate from, LocalDate to);

	List<DailySalesDTO> getQuarterlySalesReport(LocalDate from, LocalDate to);

	List<DailySalesDTO> getYearlySalesReport(LocalDate from, LocalDate to);

}

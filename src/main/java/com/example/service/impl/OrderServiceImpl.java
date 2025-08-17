package com.example.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.dto.DailySalesDTO;
import com.example.dto.ProductSalesDTO;
import com.example.dto.SalesReportDTO;
import com.example.model.Cart;
import com.example.model.Orders;
import com.example.model.ProductOrder;
import com.example.repository.CartRepository;
import com.example.repository.OrdersRepository;
import com.example.service.OrderService;
import com.example.util.CommonUtil;
import com.example.util.OrderStatus;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Override
	@Transactional
	public void saveOrder(Integer userId, Orders order) throws Exception {
		List<Cart> carts = cartRepository.findByUserId(userId);
		if (carts.isEmpty())
			return;

		order.setOrderId(UUID.randomUUID().toString());
		order.setOrderDate(LocalDate.now());
		order.setStatus(OrderStatus.IN_PROGRESS.getName());
		order.setUser(carts.get(0).getUser());

		List<ProductOrder> productOrderList = new ArrayList<>();
		double total = 0;

		for (Cart cart : carts) {
			ProductOrder productOrder = new ProductOrder();
			productOrder.setProduct(cart.getProduct());
			productOrder.setQuantity(cart.getQuantity());
			productOrder.setPrice(cart.getProduct().getDiscountPrice());
			productOrder.setOrder(order);

			total += productOrder.getPrice() * productOrder.getQuantity();
			productOrderList.add(productOrder);
		}

		order.setProductOrders(productOrderList);
		order.setTotalAmount(total);

		// Ensure order address is set
		if (order.getOrderAddress() == null) {
			throw new IllegalArgumentException("Order address is required");
		}

		Orders savedOrder = ordersRepository.save(order);

		commonUtil.sendMailForProductOrder(savedOrder, "success");

		cartRepository.deleteAll(carts);
	}

	@Override
	public List<Orders> getOrdersByUser(Integer userId) {
		List<Orders> orders = ordersRepository.findByUserId(userId);
		for (Orders order : orders) {
			double total = 0;
			for (ProductOrder po : order.getProductOrders()) {
				total += po.getPrice() * po.getQuantity();
			}
			order.setTotalAmount(total);
		}
		return orders;
	}

	@Override
	public Orders getOrdersById(Integer id) {
		Optional<Orders> optionalOrder = ordersRepository.findById(id);
		return optionalOrder.orElse(null);
	}

	@Override
	public Orders updateOrderStatus(Integer orderId, String status) {
		Optional<Orders> orderOpt = ordersRepository.findById(orderId);
		if (orderOpt.isPresent()) {
			Orders order = orderOpt.get();
			order.setStatus(status);
			return ordersRepository.save(order);
		}
		return null;
	}

	@Override
	public List<Orders> getAllOrders() {
		return ordersRepository.findAll();
	}

	@Override
	public Page<Orders> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return ordersRepository.findAll(pageable);
	}

	@Override
	public long getOrderCount() {
		return ordersRepository.count();
	}

	@Override
	public Orders getOrdersByOrderId(String orderId) {
		return ordersRepository.findByOrderId(orderId);
	}

	@Override
	public List<DailySalesDTO> getSalesReportBetween(LocalDate from, LocalDate to) {
		// Fetch daily order stats
		List<Object[]> results = ordersRepository.getOrderStatsBetween(from, to);

		// List to store daily sales data
		List<DailySalesDTO> sales = new ArrayList<>();

		// Add each day's data to the sales list
		for (Object[] row : results) {
			LocalDate date = (LocalDate) row[0];
			Long totalOrders = (Long) row[1];
			Double totalRevenue = (Double) row[2];

			sales.add(new DailySalesDTO(date, totalOrders, totalRevenue));
		}

		return sales;
	}

	public Double calculateTotalRevenue(LocalDate startDate, LocalDate endDate) {
		return ordersRepository.getTotalRevenueBetweenDates(startDate, endDate);
	}

	@Override
	public Page<Orders> getOrdersByStatus(String status, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("orderDate").descending());
		return ordersRepository.findByStatus(status, pageable);
	}

	@Override
	public SalesReportDTO getSalesReport(String reportType, LocalDate from, LocalDate to) {
		List<DailySalesDTO> salesData;
		List<Object[]> topProductsData = ordersRepository.getTopSellingProducts(from, to);
		List<ProductSalesDTO> topSellingProducts = new ArrayList<>();

		// Convert top products data to DTOs
		for (Object[] row : topProductsData) {
			topSellingProducts.add(new ProductSalesDTO((String) row[0], // productName
					(String) row[1], // category
					(Long) row[2], // totalQuantity
					(Double) row[3] // totalRevenue
			));
		}

		// Get sales data based on report type
		switch (reportType.toUpperCase()) {
		case "MONTHLY":
			salesData = getMonthlySalesReport(from, to);
			break;
		case "QUARTERLY":
			salesData = getQuarterlySalesReport(from, to);
			break;
		case "YEARLY":
			salesData = getYearlySalesReport(from, to);
			break;
		default: // DAILY
			salesData = getSalesReportBetween(from, to);
		}

		// Calculate totals
		Double totalRevenue = calculateTotalRevenue(from, to);
		Long totalOrders = salesData.stream().mapToLong(DailySalesDTO::getTotalOrders).sum();

		return new SalesReportDTO(reportType, from, to, salesData, totalRevenue, totalOrders, topSellingProducts);
	}

	@Override
	public List<DailySalesDTO> getMonthlySalesReport(LocalDate from, LocalDate to) {
		List<Object[]> results = ordersRepository.getMonthlyOrderStats(from, to);
		List<DailySalesDTO> sales = new ArrayList<>();

		for (Object[] row : results) {
			String monthStr = (String) row[0];
			LocalDate date = LocalDate.parse(monthStr + "-01"); // First day of the month
			Long totalOrders = (Long) row[1];
			Double totalRevenue = (Double) row[2];
			sales.add(new DailySalesDTO(date, totalOrders, totalRevenue));
		}

		return sales;
	}

	@Override
	public List<DailySalesDTO> getQuarterlySalesReport(LocalDate from, LocalDate to) {
		List<Object[]> results = ordersRepository.getQuarterlyOrderStats(from, to);
		List<DailySalesDTO> sales = new ArrayList<>();

		for (Object[] row : results) {
			String quarterStr = (String) row[0];
			String[] parts = quarterStr.split("-Q");
			int year = Integer.parseInt(parts[0]);
			int quarter = Integer.parseInt(parts[1]);
			LocalDate date = LocalDate.of(year, (quarter - 1) * 3 + 1, 1); // First day of the quarter
			Long totalOrders = (Long) row[1];
			Double totalRevenue = (Double) row[2];
			sales.add(new DailySalesDTO(date, totalOrders, totalRevenue));
		}

		return sales;
	}

	@Override
	public List<DailySalesDTO> getYearlySalesReport(LocalDate from, LocalDate to) {
		List<Object[]> results = ordersRepository.getYearlyOrderStats(from, to);
		List<DailySalesDTO> sales = new ArrayList<>();

		for (Object[] row : results) {
			int year = (Integer) row[0];
			LocalDate date = LocalDate.of(year, 1, 1); // First day of the year
			Long totalOrders = (Long) row[1];
			Double totalRevenue = (Double) row[2];
			sales.add(new DailySalesDTO(date, totalOrders, totalRevenue));
		}

		return sales;
	}

}

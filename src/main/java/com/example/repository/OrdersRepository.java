package com.example.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

	// Find by generated order ID (UUID string)
	Orders findByOrderId(String orderId);

	// Find all orders by a specific user
	List<Orders> findByUserId(Integer userId);

	// Count orders by status
	long countByStatus(String status);

	@Query("SELECT o.orderDate, COUNT(o), SUM(o.totalAmount) FROM Orders o WHERE o.orderDate BETWEEN :from AND :to GROUP BY o.orderDate ORDER BY o.orderDate")
	List<Object[]> getOrderStatsBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

	@Query("SELECT SUM(o.totalAmount) FROM Orders o WHERE o.orderDate BETWEEN :startDate AND :endDate")
	Double getTotalRevenueBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	Page<Orders> findByStatus(String status, Pageable pageable);

	// New queries for different time periods
	@Query("SELECT DATE_FORMAT(o.orderDate, '%Y-%m') as month, COUNT(o), SUM(o.totalAmount) " +
		   "FROM Orders o WHERE o.orderDate BETWEEN :from AND :to " +
		   "GROUP BY DATE_FORMAT(o.orderDate, '%Y-%m') ORDER BY month")
	List<Object[]> getMonthlyOrderStats(@Param("from") LocalDate from, @Param("to") LocalDate to);

	@Query("SELECT CONCAT(YEAR(o.orderDate), '-Q', QUARTER(o.orderDate)) as quarter, COUNT(o), SUM(o.totalAmount) " +
		   "FROM Orders o WHERE o.orderDate BETWEEN :from AND :to " +
		   "GROUP BY CONCAT(YEAR(o.orderDate), '-Q', QUARTER(o.orderDate)) ORDER BY quarter")
	List<Object[]> getQuarterlyOrderStats(@Param("from") LocalDate from, @Param("to") LocalDate to);

	@Query("SELECT YEAR(o.orderDate) as year, COUNT(o), SUM(o.totalAmount) " +
		   "FROM Orders o WHERE o.orderDate BETWEEN :from AND :to " +
		   "GROUP BY YEAR(o.orderDate) ORDER BY year")
	List<Object[]> getYearlyOrderStats(@Param("from") LocalDate from, @Param("to") LocalDate to);

	// Query for top selling products
	@Query("SELECT p.title as productName, p.category, SUM(po.quantity) as totalQuantity, SUM(po.price * po.quantity) as totalRevenue " +
		   "FROM ProductOrder po JOIN po.product p JOIN po.order o " +
		   "WHERE o.orderDate BETWEEN :from AND :to " +
		   "GROUP BY p.title, p.category " +
		   "ORDER BY totalQuantity DESC")
	List<Object[]> getTopSellingProducts(@Param("from") LocalDate from, @Param("to") LocalDate to);

}

package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

	// Get all ProductOrders for a given user ID (via the Orders relationship)
	@Query("SELECT po FROM ProductOrder po WHERE po.order.user.id = :userId")
	List<ProductOrder> findByUserId(Integer userId);

	// Get all ProductOrders linked to a given orderId
	@Query("SELECT po FROM ProductOrder po WHERE po.order.orderId = :orderId")
	List<ProductOrder> findByOrderId(String orderId);
}

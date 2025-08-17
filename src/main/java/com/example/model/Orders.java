package com.example.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String orderId;
	private LocalDate orderDate;
	private String status;
	private String paymentType;
	private String paymentId;

	@ManyToOne
	private UserDtls user;

	@OneToOne(cascade = CascadeType.ALL)
	private OrderAddress orderAddress;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductOrder> productOrders;

	@Column(name = "total_amount")
	private double totalAmount;

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public UserDtls getUser() {
		return user;
	}

	public void setUser(UserDtls user) {
		this.user = user;
	}

	public OrderAddress getOrderAddress() {
		return orderAddress;
	}

	public void setOrderAddress(OrderAddress orderAddress) {
		this.orderAddress = orderAddress;
	}

	public List<ProductOrder> getProductOrders() {
		return productOrders;
	}

	public void setProductOrders(List<ProductOrder> productOrders) {
		this.productOrders = productOrders;
	}
}

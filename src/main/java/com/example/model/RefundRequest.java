package com.example.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refund_requests")
public class RefundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    private String status; // PENDING, APPROVED, REJECTED, COMPLETED
    private Double amount;
    private String reason;
    private LocalDateTime requestDate;
    private LocalDateTime processedDate;
    private String refundId; // For RazorPay refund ID
    private String paymentMethod; // Store the payment method from the order

    // Getters
    public Integer getId() {
        return id;
    }

    public Orders getOrder() {
        return order;
    }

    public String getStatus() {
        return status;
    }

    public Double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public LocalDateTime getProcessedDate() {
        return processedDate;
    }

    public String getRefundId() {
        return refundId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
} 
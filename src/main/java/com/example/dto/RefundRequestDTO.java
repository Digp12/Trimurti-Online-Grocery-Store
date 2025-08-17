package com.example.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RefundRequestDTO {
    private Integer id;
    private Integer orderId;
    private String status;
    private Double amount;
    private String reason;
    private LocalDateTime requestDate;
    private LocalDateTime processedDate;
    private String paymentType;
    private String paymentMethod;
    private String refundId;
    private String orderNumber;
    private String userName;
    private String userEmail;

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getOrderId() {
        return orderId;
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

    public String getPaymentType() {
        return paymentType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getRefundId() {
        return refundId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
} 
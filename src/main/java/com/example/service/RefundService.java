package com.example.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.dto.RefundRequestDTO;
import com.example.model.RefundRequest;

public interface RefundService {
    
    RefundRequestDTO createRefundRequest(RefundRequestDTO refundRequestDTO);
    
    RefundRequestDTO updateRefundStatus(Integer refundId, String status);
    
    List<RefundRequestDTO> getRefundRequestsByUserId(Integer userId);
    
    List<RefundRequestDTO> getRefundRequestsByOrderId(Integer orderId);
    
    List<RefundRequestDTO> getPendingRefundRequests();
    
    RefundRequestDTO getRefundRequestById(Integer refundId);
    
    boolean processRazorpayRefund(RefundRequest refundRequest);
    
    // New methods for pagination and status counts
    Page<RefundRequestDTO> getAllRefundRequestsPagination(Integer pageNo, Integer pageSize);
    
    Page<RefundRequestDTO> getRefundRequestsByStatusPagination(String status, Integer pageNo, Integer pageSize);
    
    long getRefundCountByStatus(String status);
} 
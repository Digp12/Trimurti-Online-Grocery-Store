package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.RefundRequest;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Integer> {
    
    List<RefundRequest> findByOrderId(Integer orderId);
    
    @Query("SELECT r FROM RefundRequest r JOIN FETCH r.order o JOIN FETCH o.user u WHERE u.id = :userId")
    List<RefundRequest> findByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT r FROM RefundRequest r WHERE r.order.paymentType = 'ONLINE' AND r.status = 'PENDING'")
    List<RefundRequest> findPendingOnlineRefunds();
    
    // New methods for pagination and status counts
    Page<RefundRequest> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM RefundRequest r WHERE r.status = :status")
    long countByStatus(@Param("status") String status);
} 
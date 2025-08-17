package com.example.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.RefundRequestDTO;
import com.example.model.Orders;
import com.example.model.RefundRequest;
import com.example.repository.OrdersRepository;
import com.example.repository.RefundRequestRepository;
import com.example.repository.UserRepository;
import com.example.service.RefundService;
import com.example.util.CommonUtil;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;

@Service
@Transactional
public class RefundServiceImpl implements RefundService {

	private static final Logger logger = LoggerFactory.getLogger(RefundServiceImpl.class);

	@Autowired
	private RefundRequestRepository refundRequestRepository;

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private RazorpayClient razorpayClient;

	@Override
	public RefundRequestDTO createRefundRequest(RefundRequestDTO refundRequestDTO) {
		logger.info("Creating refund request for order ID: {}", refundRequestDTO.getOrderId());

		// Validate reason
		if (refundRequestDTO.getReason() == null || refundRequestDTO.getReason().trim().isEmpty()) {
			throw new RuntimeException("Refund reason cannot be empty");
		}

		Orders order = ordersRepository.findById(refundRequestDTO.getOrderId())
				.orElseThrow(() -> new RuntimeException("Order not found"));

		// Check for existing refund requests
		List<RefundRequest> existingRefunds = refundRequestRepository.findByOrderId(order.getId());
		if (!existingRefunds.isEmpty()) {
			RefundRequest existingRefund = existingRefunds.get(0);
			if (existingRefund.getStatus().equalsIgnoreCase("PENDING")) {
				throw new RuntimeException("A pending refund request already exists for this order");
			}
			if (existingRefund.getStatus().equalsIgnoreCase("APPROVED")
					|| existingRefund.getStatus().equalsIgnoreCase("COMPLETED")) {
				throw new RuntimeException("A refund has already been processed for this order");
			}
		}

		// Check order status
		if (!order.getStatus().equalsIgnoreCase("in progress") && !order.getStatus().equalsIgnoreCase("cancelled")
				&& !order.getStatus().equalsIgnoreCase("delivered")) {
			throw new RuntimeException(
					"Refund can only be requested for orders that are in progress, cancelled, or delivered");
		}

		// No refunds for COD orders
		if (order.getPaymentType().equalsIgnoreCase("COD")) {
			throw new RuntimeException("Refund cannot be processed for cash on delivery orders");
		}

		// Create and save refund request
		RefundRequest refundRequest = new RefundRequest();
		refundRequest.setOrder(order);
		refundRequest.setStatus("PENDING");
		refundRequest.setAmount(order.getTotalAmount());
		refundRequest.setReason(refundRequestDTO.getReason().trim());
		refundRequest.setRequestDate(LocalDateTime.now());
		refundRequest.setPaymentMethod(order.getPaymentType());

		try {
			refundRequest = refundRequestRepository.save(refundRequest);
			logger.info("Refund request created successfully with ID: {}", refundRequest.getId());
			return convertToDTO(refundRequest);
		} catch (Exception e) {
			logger.error("Failed to create refund request for order {}: {}", order.getId(), e.getMessage());
			throw new RuntimeException("Failed to create refund request: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public RefundRequestDTO updateRefundStatus(Integer refundId, String status) {
		logger.info("Updating refund status for refund ID: {} to status: {}", refundId, status);

		// Validate input parameters
		if (refundId == null || refundId <= 0) {
			logger.error("Invalid refund ID provided: {}", refundId);
			throw new RuntimeException("Invalid refund ID");
		}

		// Validate status
		if (!isValidStatus(status)) {
			logger.error("Invalid refund status provided: {}", status);
			throw new RuntimeException("Invalid refund status: " + status);
		}

		// Find refund request
		RefundRequest refundRequest = refundRequestRepository.findById(refundId).orElseThrow(() -> {
			logger.error("Refund request not found for ID: {}", refundId);
			return new RuntimeException("Refund request not found");
		});

		// Check if status can be updated
		if (!canUpdateStatus(refundRequest.getStatus(), status)) {
			logger.error("Invalid status transition from {} to {} for refund ID: {}", refundRequest.getStatus(), status,
					refundId);
			throw new RuntimeException("Cannot update status from " + refundRequest.getStatus() + " to " + status);
		}

		// Validate order status for approval
		if (status.equalsIgnoreCase("APPROVED")
				&& !refundRequest.getOrder().getStatus().equalsIgnoreCase("cancelled")) {
			logger.error("Cannot approve refund for non-cancelled order. Order status: {}",
					refundRequest.getOrder().getStatus());
			throw new RuntimeException("Cannot approve refund for non-cancelled order");
		}

		// Store the old status before changing
		String oldStatus = refundRequest.getStatus();
		refundRequest.setStatus(status);
		refundRequest.setProcessedDate(LocalDateTime.now());

		try {
			// Process Razorpay refund for approved refunds
			if (status.equalsIgnoreCase("APPROVED")
					&& refundRequest.getOrder().getPaymentType().equalsIgnoreCase("ONLINE")) {
				processRazorpayRefund(refundRequest);
			}

			// Save the updated refund request
			refundRequest = refundRequestRepository.save(refundRequest);
			logger.info("Refund status updated successfully from {} to {} for refund ID: {}", oldStatus, status,
					refundId);

			// Send email for refund status update
			try {
				commonUtil.sendMailForRefund(refundRequest);
				logger.info("Refund status update email sent successfully for refund ID: {}", refundId);
			} catch (Exception e) {
				logger.error("Failed to send refund status update email for refund ID: {}: {}", refundId,
						e.getMessage());
				// Don't throw error to prevent refund status update failure
			}

			return convertToDTO(refundRequest);
		} catch (Exception e) {
			logger.error("Failed to update refund status for refund ID: {}: {}", refundId, e.getMessage());
			throw new RuntimeException("Failed to update refund status: " + e.getMessage());
		}
	}

	private boolean isValidStatus(String status) {
		if (status == null || status.trim().isEmpty()) {
			return false;
		}
		String normalizedStatus = status.toUpperCase();
		return normalizedStatus.equals("PENDING") || normalizedStatus.equals("APPROVED")
				|| normalizedStatus.equals("REJECTED") || normalizedStatus.equals("COMPLETED")
				|| normalizedStatus.equals("FAILED");
	}

	private boolean canUpdateStatus(String currentStatus, String newStatus) {
		if (currentStatus == null || newStatus == null) {
			return false;
		}

		currentStatus = currentStatus.toUpperCase();
		newStatus = newStatus.toUpperCase();

		// Cannot update completed or failed refunds
		if (currentStatus.equals("COMPLETED") || currentStatus.equals("FAILED")) {
			return false;
		}

		// Define valid status transitions
		switch (currentStatus) {
		case "PENDING":
			return newStatus.equals("APPROVED") || newStatus.equals("REJECTED");
		case "APPROVED":
			return newStatus.equals("COMPLETED") || newStatus.equals("FAILED");
		case "REJECTED":
			return false;
		default:
			return false;
		}
	}

	@Override
	public List<RefundRequestDTO> getRefundRequestsByOrderId(Integer orderId) {
		return refundRequestRepository.findByOrderId(orderId).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<RefundRequestDTO> getRefundRequestsByUserId(Integer userId) {
		return refundRequestRepository.findByUserId(userId).stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<RefundRequestDTO> getPendingRefundRequests() {
		return refundRequestRepository.findPendingOnlineRefunds().stream().map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public RefundRequestDTO getRefundRequestById(Integer refundId) {
		return refundRequestRepository.findById(refundId).map(this::convertToDTO)
				.orElseThrow(() -> new RuntimeException("Refund request not found"));
	}

	@Override
	public boolean processRazorpayRefund(RefundRequest refundRequest) {
		try {
			if (!refundRequest.getOrder().getPaymentType().equals("ONLINE")) {
				throw new RuntimeException("Refund can only be processed for online payments");
			}

			if (refundRequest.getOrder().getPaymentId() == null || refundRequest.getOrder().getPaymentId().isEmpty()) {
				logger.warn("Payment ID not found for order {}. Cannot process Razorpay refund.",
						refundRequest.getOrder().getId());
				refundRequest.setStatus("COMPLETED");
				refundRequest.setRefundId("MANUAL_REFUND_" + refundRequest.getId());
				refundRequestRepository.save(refundRequest);
				logger.info("Marked refund as completed manually for order {} due to missing payment ID",
						refundRequest.getOrder().getId());
				return true;
			}

			if (refundRequest.getRefundId() != null && !refundRequest.getRefundId().isEmpty()) {
				logger.warn("Refund already processed for order {} with Razorpay refund ID {}",
						refundRequest.getOrder().getId(), refundRequest.getRefundId());
				return true;
			}

			if (refundRequest.getAmount() <= 0) {
				throw new RuntimeException("Invalid refund amount: " + refundRequest.getAmount());
			}

			if (refundRequest.getAmount() > refundRequest.getOrder().getTotalAmount()) {
				throw new RuntimeException("Refund amount cannot exceed order amount");
			}

			logger.info("Initiating Razorpay refund for order {} with amount {}", refundRequest.getOrder().getId(),
					refundRequest.getAmount());

			JSONObject refundRequestJson = new JSONObject();
			refundRequestJson.put("amount", refundRequest.getAmount() * 100); // Convert to paise
			refundRequestJson.put("speed", "normal");
			refundRequestJson.put("notes", new JSONObject().put("reason", refundRequest.getReason()).put("order_id",
					refundRequest.getOrder().getId()));

			Refund refund = razorpayClient.payments.refund(refundRequest.getOrder().getPaymentId(), refundRequestJson);

			refundRequest.setRefundId(refund.get("id"));
			refundRequest.setStatus("COMPLETED");
			refundRequestRepository.save(refundRequest);

			logger.info("Refund processed successfully for order {} with Razorpay refund ID {}",
					refundRequest.getOrder().getId(), refund.get("id"));

			return true;
		} catch (RazorpayException e) {
			logger.error("Failed to process Razorpay refund for order {}: {}", refundRequest.getOrder().getId(),
					e.getMessage());
			refundRequest.setStatus("FAILED");
			refundRequest.setRefundId("FAILED_" + refundRequest.getId());
			refundRequestRepository.save(refundRequest);
			throw new RuntimeException("Failed to process refund: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Unexpected error while processing refund for order {}: {}", refundRequest.getOrder().getId(),
					e.getMessage());
			refundRequest.setStatus("FAILED");
			refundRequest.setRefundId("FAILED_" + refundRequest.getId());
			refundRequestRepository.save(refundRequest);
			throw new RuntimeException("Unexpected error while processing refund: " + e.getMessage());
		}
	}

	@Override
	public Page<RefundRequestDTO> getAllRefundRequestsPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("requestDate").descending());
		Page<RefundRequest> page = refundRequestRepository.findAll(pageable);
		return page.map(this::convertToDTO);
	}

	@Override
	public Page<RefundRequestDTO> getRefundRequestsByStatusPagination(String status, Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("requestDate").descending());
		Page<RefundRequest> page = refundRequestRepository.findByStatus(status, pageable);
		return page.map(this::convertToDTO);
	}

	@Override
	public long getRefundCountByStatus(String status) {
		return refundRequestRepository.countByStatus(status);
	}

	private RefundRequestDTO convertToDTO(RefundRequest refundRequest) {
		RefundRequestDTO dto = new RefundRequestDTO();
		dto.setId(refundRequest.getId());
		dto.setOrderId(refundRequest.getOrder().getId());
		dto.setStatus(refundRequest.getStatus());
		dto.setAmount(refundRequest.getAmount());
		dto.setReason(refundRequest.getReason());
		dto.setRequestDate(refundRequest.getRequestDate());
		dto.setProcessedDate(refundRequest.getProcessedDate());
		dto.setPaymentType(refundRequest.getOrder().getPaymentType());
		dto.setPaymentMethod(refundRequest.getPaymentMethod());
		dto.setRefundId(refundRequest.getRefundId());
		dto.setOrderNumber(refundRequest.getOrder().getOrderId());
		dto.setUserName(refundRequest.getOrder().getUser().getName());
		dto.setUserEmail(refundRequest.getOrder().getUser().getEmail());
		return dto;
	}
}

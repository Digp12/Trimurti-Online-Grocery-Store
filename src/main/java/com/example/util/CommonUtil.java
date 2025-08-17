package com.example.util;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.model.Orders;
import com.example.model.ProductOrder;
import com.example.model.RefundRequest;
import com.example.model.UserDtls;
import com.example.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserService userService;

	public Boolean sendMail(String url, String recipientEmail) throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("storetrimurtikirana@gmail.com", "Trimurti-Online Grocery store");
		helper.setTo(recipientEmail);

		UserDtls userDtls = userService.getUserByEmail(recipientEmail);

		String userName = (userDtls != null) ? userDtls.getName() : "User";

		String content = "<p>Hello " + userName + ",</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>" + "<p>If you did not request this, please ignore this email.</p>";

		helper.setSubject("Reset Your Password");
		helper.setText(content, true);
		mailSender.send(message);

		return true;
	}

	public static String generateUrl(HttpServletRequest request) {

		// http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();

		return siteUrl.replace(request.getServletPath(), "");
	}

	String msg = null;

	public Boolean sendMailForProductOrder(Orders order, String status) throws Exception {
		if (order == null || order.getOrderAddress() == null || order.getProductOrders() == null) {
			throw new IllegalArgumentException("Invalid order details");
		}

		double totalAmount = getOrderTotal(order);

		StringBuilder productDetails = new StringBuilder();
		productDetails.append("<table border='1' cellspacing='0' cellpadding='5' style='border-collapse: collapse;'>")
				.append("<tr style='background-color: #f2f2f2;'>")
				.append("<th>Product Name</th><th>Category</th><th>Quantity</th><th>Price</th><th>Subtotal</th>")
				.append("</tr>");

		for (ProductOrder productOrder : order.getProductOrders()) {
			double subtotal = productOrder.getPrice() * productOrder.getQuantity();
			productDetails.append("<tr>").append("<td>").append(productOrder.getProduct().getTitle()).append("</td>")
					.append("<td>").append(productOrder.getProduct().getCategory()).append("</td>").append("<td>")
					.append(productOrder.getQuantity()).append("</td>").append("<td>₹").append(productOrder.getPrice())
					.append("</td>").append("<td>₹").append(subtotal).append("</td>").append("</tr>");
		}

		productDetails.append("<tr style='font-weight: bold;'>").append("<td colspan='4' align='right'>Total:</td>")
				.append("<td>₹").append(totalAmount).append("</td>").append("</tr>").append("</table>");

		String msg = "<p>Hello <b>[[name]]</b>,</p>"
				+ "<p>Thank you for your order at <b>Trimurti Kirana Stores</b>.</p>"
				+ "<p>The current status of your order is: <b>[[orderStatus]]</b>.</p>" + "<p><b>Order Details:</b></p>"
				+ productDetails + "<p><b>Payment Type:</b> [[paymentType]]</p>" + "<p><b>Order Id:</b> [[orderId]]</p>"
				+ "<p>If you have any questions, feel free to reply to this email or contact our support team.</p>"
				+ "<p>Best regards,<br><b>Trimurti Kirana Stores Team</b></p>";

		msg = msg.replace("[[name]]", order.getOrderAddress().getFirstName()).replace("[[orderStatus]]", status)
				.replace("[[paymentType]]", order.getPaymentType()).replace("[[orderId]]", order.getOrderId());

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("storetrimurtikirana@gmail.com", "Trimurti-Online Grocery Store");
		helper.setTo(order.getOrderAddress().getEmail());
		helper.setSubject("Your Order Status: " + status);
		helper.setText(msg, true);

		mailSender.send(message);
		return true;
	}

	public UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

	public double getOrderTotal(Orders order) {
		return order.getProductOrders().stream().mapToDouble(po -> po.getPrice() * po.getQuantity()).sum();
	}

	public Boolean sendMailForRefund(RefundRequest refundRequest) throws Exception {
		if (refundRequest == null || refundRequest.getOrder() == null) {
			throw new IllegalArgumentException("Invalid refund request details");
		}

		String msg = "<p>Hello <b>[[name]]</b>,</p>"
				+ "<p>Your refund request for order <b>[[orderId]]</b> has been <b>[[status]]</b>.</p>"
				+ "<p><b>Refund Details:</b></p>" + "<p>Amount: ₹[[amount]]</p>" + "<p>Reason: [[reason]]</p>"
				+ "<p>Request Date: [[requestDate]]</p>" + "<p>Processed Date: [[processedDate]]</p>"
				+ "<p>If you have any questions, feel free to reply to this email or contact our support team.</p>"
				+ "<p>Best regards,<br><b>Trimurti Kirana Stores Team</b></p>";

		msg = msg.replace("[[name]]", refundRequest.getOrder().getUser().getName())
				.replace("[[orderId]]", refundRequest.getOrder().getOrderId())
				.replace("[[status]]", refundRequest.getStatus())
				.replace("[[amount]]", String.format("%.2f", refundRequest.getAmount()))
				.replace("[[reason]]", refundRequest.getReason())
				.replace("[[requestDate]]", refundRequest.getRequestDate().toString()).replace("[[processedDate]]",
						refundRequest.getProcessedDate() != null ? refundRequest.getProcessedDate().toString()
								: "Not processed yet");

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("storetrimurtikirana@gmail.com", "Trimurti-Online Grocery Store");
		helper.setTo(refundRequest.getOrder().getUser().getEmail());
		helper.setSubject(
				"Refund Request " + refundRequest.getStatus() + " - Order #" + refundRequest.getOrder().getOrderId());
		helper.setText(msg, true);

		mailSender.send(message);
		return true;
	}

}

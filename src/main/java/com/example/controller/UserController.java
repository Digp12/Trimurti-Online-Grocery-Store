package com.example.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.dto.RefundRequestDTO;
import com.example.model.Cart;
import com.example.model.Category;
import com.example.model.Orders;
import com.example.model.UserDtls;
import com.example.service.CartService;
import com.example.service.CategoryService;
import com.example.service.OrderService;
import com.example.service.RefundService;
import com.example.service.UserService;
import com.example.util.CommonUtil;
import com.example.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RefundService refundService;

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		} else {
			session.setAttribute("succMsg", "Product added to cart");
		}

		return "redirect:/product/" + pid;
	}

	@GetMapping("/addtoCartfrompr")
	public String addToCartfromproduct(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		} else {
			session.setAttribute("succMsg", "Product added to cart");
		}

		return "redirect:/products";
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {

		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/cart";
	}

	@GetMapping("/removeItemCart/{cid}")
	public String removeItemFromCart(@PathVariable("cid") int cartId, HttpSession session, Principal p, Model m) {
		try {
			cartService.removeItem(cartId);
			session.setAttribute("succMsg", "Item removed from cart successfully.");
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Failed to remove item from cart.");
			e.printStackTrace();
		}

		return "redirect:/user/cart";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

	@GetMapping("/orders")
	public String orderPage(Principal principal, Model model) {

		UserDtls user = getLoggedInUserDetails(principal);

		List<Cart> carts = cartService.getCartsByUser(user.getId());

		model.addAttribute("user", user);
		model.addAttribute("carts", carts);

		if (!carts.isEmpty()) {
			Cart latestCart = carts.get(carts.size() - 1);
			Double orderPrice = latestCart.getTotalOrderPrice();
			Double totalOrderPrice = orderPrice + 100;

			model.addAttribute("orderPrice", orderPrice);
			model.addAttribute("totalOrderPrice", totalOrderPrice);
		}

		return "/user/order";
	}

	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute Orders order, Principal p) throws Exception {
		UserDtls user = getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), order);
		return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}

	@GetMapping("/user-orders")
	public String myOrders(Model m, Principal p) {
		UserDtls loginUser = getLoggedInUserDetails(p);
		List<Orders> userOrders = orderService.getOrdersByUser(loginUser.getId());

		// Get all refund requests for the user's orders
		List<RefundRequestDTO> refundRequests = refundService.getRefundRequestsByUserId(loginUser.getId());

		// Create a map of order IDs to refund status
		Map<Integer, String> orderRefundStatus = new HashMap<>();
		for (RefundRequestDTO refund : refundRequests) {
			orderRefundStatus.put(refund.getOrderId(), refund.getStatus());
		}

		// Calculate counts for each order status
		long inProgressCount = userOrders.stream().filter(order -> order.getStatus().equals("In Progress")).count();
		long deliveredCount = userOrders.stream().filter(order -> order.getStatus().equals("Delivered")).count();
		long cancelledCount = userOrders.stream().filter(order -> order.getStatus().equals("Cancelled")).count();
		long refundCount = refundRequests.size();

		m.addAttribute("orders", userOrders);
		m.addAttribute("orderRefundStatus", orderRefundStatus);
		m.addAttribute("inProgressCount", inProgressCount);
		m.addAttribute("deliveredCount", deliveredCount);
		m.addAttribute("cancelledCount", cancelledCount);
		m.addAttribute("refundCount", refundCount);
		return "/user/my_orders";
	}

	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
		Orders existingOrder = orderService.getOrdersById(id);

		if (existingOrder == null) {
			session.setAttribute("errorMsg", "Order not found.");
			return "redirect:/user/user-orders";
		}

		if (st == OrderStatus.CANCEL.getId()) {
			if (!existingOrder.getStatus().equalsIgnoreCase(OrderStatus.IN_PROGRESS.getName())) {
				session.setAttribute("errorMsg", "Only IN_PROGRESS orders can be cancelled.");
				return "redirect:/user/user-orders";
			}

			// Create refund request for online payments
			if (existingOrder.getPaymentType().equalsIgnoreCase("ONLINE")) {
				try {
					// Check if a refund request already exists
					List<RefundRequestDTO> existingRefunds = refundService.getRefundRequestsByOrderId(id);
					if (!existingRefunds.isEmpty()) {
						session.setAttribute("errorMsg", "A refund request already exists for this order.");
						return "redirect:/user/user-orders";
					}

					RefundRequestDTO refundRequestDTO = new RefundRequestDTO();
					refundRequestDTO.setOrderId(existingOrder.getId());
					refundRequestDTO.setReason("Order cancelled by user");
					refundService.createRefundRequest(refundRequestDTO);
					session.setAttribute("succMsg", "Order cancelled successfully. Refund request has been initiated.");
				} catch (Exception e) {
					session.setAttribute("errorMsg",
							"Order cancelled but failed to create refund request. Please contact support.");
					e.printStackTrace();
				}
			} else {
				session.setAttribute("succMsg", "Order cancelled successfully.");
			}
		}

		// Find the corresponding status name based on the ID
		String status = null;
		for (OrderStatus orderSt : OrderStatus.values()) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
				break;
			}
		}

		Orders updatedOrder = orderService.updateOrderStatus(id, status);
		if (updatedOrder == null) {
			session.setAttribute("errorMsg", "Failed to update order status.");
		}
		return "redirect:/user/user-orders";
	}

	@GetMapping("profile")
	public String profile() {
		return "/user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password not updated !! Error in server");
			} else {
				session.setAttribute("succMsg", "Password Updated sucessfully");
			}
		} else {
			session.setAttribute("errorMsg", "Current Password incorrect");
		}

		return "redirect:/user/profile";
	}

}

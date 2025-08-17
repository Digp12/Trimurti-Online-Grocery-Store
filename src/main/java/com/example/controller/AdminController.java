package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.dto.RefundRequestDTO;
import com.example.dto.SalesReportDTO;
import com.example.model.Category;
import com.example.model.Orders;
import com.example.model.Product;
import com.example.model.UserDtls;
import com.example.service.CartService;
import com.example.service.CategoryService;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.example.service.RefundService;
import com.example.service.UserService;
import com.example.util.CommonUtil;
import com.example.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

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

	@GetMapping("/")
	public String getAdminDashboard(Model model) {
		long productCount = productService.getProductCount();
		long orderCount = orderService.getOrderCount();
		long userCount = userService.getUserCount();
		long categoryCount = categoryService.getCategoryCount();

		// Get refund request statistics using pagination
		Page<RefundRequestDTO> refundPage = refundService.getAllRefundRequestsPagination(0, Integer.MAX_VALUE);
		List<RefundRequestDTO> allRefundRequests = refundPage.getContent();
		long pendingRefundCount = allRefundRequests.stream()
				.filter(refund -> refund.getStatus().equalsIgnoreCase("PENDING")).count();
		long totalRefundCount = allRefundRequests.size();

		model.addAttribute("productCount", productCount);
		model.addAttribute("orderCount", orderCount);
		model.addAttribute("userCount", userCount);
		model.addAttribute("categoryCount", categoryCount);
		model.addAttribute("pendingRefundCount", pendingRefundCount);
		model.addAttribute("totalRefundCount", totalRefundCount);

		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category(Model m, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize) {
		// m.addAttribute("categorys", categoryService.getAllCategory());
		Page<Category> page = categoryService.getAllCategorPagination(pageNo, pageSize);
		List<Category> categorys = page.getContent();
		m.addAttribute("categorys", categorys);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/category";
	}

	@GetMapping("/addCategory")
	public String addCategoryPage(Model model) {
		return "admin/add_category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam MultipartFile file, HttpSession session)
			throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = categoryService.existCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errorMsg", "Category Name already exists");
		} else {
			Category saveCategory = categoryService.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved! Internal server error");
			} else {
				String uploadDir = new File("src/main/resources/static/img/category_img").getAbsolutePath();

				File folder = new File(uploadDir);
				if (!folder.exists()) {
					folder.mkdirs();
				}

				Path path = Paths.get(uploadDir + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "Saved successfully");
			}
		}

		return "redirect:/admin/addCategory";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);

		if (deleteCategory) {
			session.setAttribute("succMsg", "category delete success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/addCategory";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(category)) {

			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}

		Category updateCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updateCategory)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Category update success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		product.setImage(imageName);

		int discount = product.getDiscount();
		if (discount < 0)
			discount = 0;
		if (discount > 100)
			discount = 100;
		product.setDiscount(discount);

		// Set discounted price
		if (discount > 0 && discount < 100) {
			product.setDiscountPrice(product.getPrice() - (product.getPrice() * discount / 100.0));
		} else {
			product.setDiscountPrice(product.getPrice());
		}

		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			String uploadDir = new File("src/main/resources/static/img/product_img").getAbsolutePath();

			File folder = new File(uploadDir);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			Path path = Paths.get(uploadDir + File.separator + image.getOriginalFilename());
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Product Saved Successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong on the server");
		}

		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/products")
	public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {

		Page<Product> page = null;
		if (ch != null && ch.length() > 0) {
			page = productService.searchProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllProductsPagination(pageNo, pageSize);
		}
		m.addAttribute("products", page.getContent());

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product delete success");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMbassg", "invalid Discount");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Product update success");
			} else {
				session.setAttribute("errorMsg", "Something wrong on server");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();
	}

	@GetMapping("/users")
	public String getAllUsers(Model m, @RequestParam Integer type) {
		List<UserDtls> users = null;
		if (type == 1) {
			users = userService.getUsers("ROLE_USER");
		} else {
			users = userService.getUsers("ROLE_ADMIN");
		}
		m.addAttribute("userType", type);
		m.addAttribute("users", users);
		return "/admin/users";
	}

	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,
			@RequestParam Integer type, HttpSession session) {
		Boolean f = userService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "Account Status Updated");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/admin/users?type=" + type;
	}

	@GetMapping("/orders")
	public String getAllOrders(Model m, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize) {

		Page<Orders> page = orderService.getAllOrdersPagination(pageNo, pageSize);
		m.addAttribute("orders", page.getContent());
		m.addAttribute("srch", false);
		m.addAttribute("statuses", OrderStatus.values());
		m.addAttribute("selectedStatus", "ALL");

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "/admin/orders";
	}

	@GetMapping("/orders-by-status")
	public String getOrdersByStatus(@RequestParam String status, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize, Model model) {

		Page<Orders> page;

		if (status.equalsIgnoreCase("ALL")) {
			page = orderService.getAllOrdersPagination(pageNo, pageSize);
		} else {
			// 🔁 Map enum name to display name
			String statusDisplayName = Arrays.stream(OrderStatus.values())
					.filter(s -> s.name().equalsIgnoreCase(status)).map(OrderStatus::getName).findFirst().orElse(null);

			page = orderService.getOrdersByStatus(statusDisplayName, pageNo, pageSize);
		}

		model.addAttribute("orders", page.getContent());
		model.addAttribute("statuses", OrderStatus.values());
		model.addAttribute("selectedStatus", status.toUpperCase());

		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());

		return "/admin/orders";
	}

	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam(required = false) Integer st, HttpSession session) {
		if (st == null || st < 0) {
			session.setAttribute("errorMsg", "Please select a valid order status");
			return "redirect:/admin/orders";
		}

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
				break;
			}
		}

		if (status == null) {
			session.setAttribute("errorMsg", "Invalid order status selected");
			return "redirect:/admin/orders";
		}

		Orders updatedOrder = orderService.updateOrderStatus(id, status);

		try {
			if (updatedOrder != null) {
				commonUtil.sendMailForProductOrder(updatedOrder, status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (updatedOrder != null) {
			session.setAttribute("succMsg", "Order status updated successfully.");
		} else {
			session.setAttribute("errorMsg", "Failed to update order status.");
		}

		return "redirect:/admin/orders";
	}

	@GetMapping("/search-order")
	public String searchOrder(@RequestParam String orderId, Model m, HttpSession session,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {

		if (orderId != null && !orderId.trim().isEmpty()) {
			Orders order = orderService.getOrdersByOrderId(orderId.trim());

			if (order == null) {
				session.setAttribute("errorMsg", "Incorrect orderId");
				m.addAttribute("orderDtls", null);
			} else {
				m.addAttribute("orderDtls", order);
			}

			m.addAttribute("srch", true);
		} else {
			Page<Orders> page = orderService.getAllOrdersPagination(pageNo, pageSize);
			m.addAttribute("orders", page.getContent());
			m.addAttribute("srch", false);

			m.addAttribute("pageNo", page.getNumber());
			m.addAttribute("pageSize", pageSize);
			m.addAttribute("totalElements", page.getTotalElements());
			m.addAttribute("totalPages", page.getTotalPages());
			m.addAttribute("isFirst", page.isFirst());
			m.addAttribute("isLast", page.isLast());
		}

		return "/admin/orders";
	}

	@GetMapping("/add-admin")
	public String loadAdminAdd() {
		return "/admin/add_admin";
	}

	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);

		UserDtls saveUser = userService.saveAdmin(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {

				String uploadDir = new File("src/main/resources/static/img/profile_img").getAbsolutePath();

				File folder = new File(uploadDir);
				if (!folder.exists()) {
					folder.mkdirs();
				}

				Path path = Paths.get(uploadDir + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Registered successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong on server");
		}

		return "redirect:/admin/add-admin";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/admin/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/admin/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);

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

		return "redirect:/admin/profile";
	}

	@GetMapping("/sales-report")
	public String getSalesReportByDate(@RequestParam(required = false) String reportType,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to, Model model) {

		if (reportType == null) {
			reportType = "DAILY";
		}

		if (from == null || to == null) {
			to = LocalDate.now();
			switch (reportType.toUpperCase()) {
			case "MONTHLY":
				from = to.minusMonths(11);
				break;
			case "QUARTERLY":
				from = to.minusMonths(35); // 3 years
				break;
			case "YEARLY":
				from = to.minusYears(4);
				break;
			default: // DAILY
				from = to.minusDays(6);
			}
		}

		SalesReportDTO reportData = orderService.getSalesReport(reportType, from, to);

		model.addAttribute("reportData", reportData.getSalesData());
		model.addAttribute("from", from);
		model.addAttribute("to", to);
		model.addAttribute("reportType", reportType);
		model.addAttribute("totalOrders", reportData.getTotalOrders());
		model.addAttribute("totalRevenue", reportData.getTotalRevenue());
		model.addAttribute("topSellingProducts", reportData.getTopSellingProducts());

		return "/admin/sales_report";
	}

	@GetMapping("/refunds")
	public String getRefundRequests(Model model, 
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "ALL") String status) {
		logger.info("Fetching refund requests with pageNo: {}, pageSize: {}, status: {}", pageNo, pageSize, status);
		
		Page<RefundRequestDTO> page;
		if (status.equalsIgnoreCase("ALL")) {
			page = refundService.getAllRefundRequestsPagination(pageNo, pageSize);
		} else {
			page = refundService.getRefundRequestsByStatusPagination(status, pageNo, pageSize);
		}
		
		List<RefundRequestDTO> refundRequests = page.getContent();
		
		// Count requests by status
		long pendingCount = refundService.getRefundCountByStatus("PENDING");
		long approvedCount = refundService.getRefundCountByStatus("APPROVED");
		long rejectedCount = refundService.getRefundCountByStatus("REJECTED");
		long completedCount = refundService.getRefundCountByStatus("COMPLETED");
		
		logger.info("Total refund requests: {}, Pending: {}, Approved: {}, Rejected: {}, Completed: {}", 
			page.getTotalElements(), pendingCount, approvedCount, rejectedCount, completedCount);
		
		model.addAttribute("refundRequests", refundRequests);
		model.addAttribute("pendingCount", pendingCount);
		model.addAttribute("approvedCount", approvedCount);
		model.addAttribute("rejectedCount", rejectedCount);
		model.addAttribute("completedCount", completedCount);
		
		// Pagination attributes
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());
		
		// Status filter
		model.addAttribute("selectedStatus", status);
		model.addAttribute("statuses", Arrays.asList("ALL", "PENDING", "APPROVED", "REJECTED", "COMPLETED"));
		
		return "admin/refunds";
	}

	@PostMapping("/refunds/{refundId}/status")
	@ResponseBody
	public ResponseEntity<?> updateRefundStatus(@PathVariable Integer refundId,
			@RequestBody Map<String, String> request, HttpSession session) {

		logger.info("Updating refund status for refund ID: {}", refundId);

		try {
			// Validate input parameters
			if (refundId == null || refundId <= 0) {
				return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Invalid refund ID"
				));
			}

			String status = request.get("status");
			if (status == null || status.trim().isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Status cannot be empty"
				));
			}

			// Update refund status
			RefundRequestDTO updatedRefund = refundService.updateRefundStatus(refundId, status);

			// Send success message
			session.setAttribute("succMsg", "Refund status updated successfully");

			logger.info("Successfully updated refund status to {} for refund ID: {}", status, refundId);

			return ResponseEntity.ok().body(Map.of(
				"success", true,
				"message", "Refund status updated successfully",
				"refund", updatedRefund
			));

		} catch (RuntimeException e) {
			logger.error("Failed to update refund status for refund ID {}: {}", refundId, e.getMessage());
			session.setAttribute("errorMsg", "Failed to update refund status: " + e.getMessage());

			return ResponseEntity.badRequest().body(Map.of(
				"success", false,
				"message", e.getMessage()
			));
		} catch (Exception e) {
			logger.error("Unexpected error while updating refund status for refund ID {}: {}", refundId, e.getMessage());
			session.setAttribute("errorMsg", "An unexpected error occurred while updating refund status");

			return ResponseEntity.internalServerError().body(Map.of(
				"success", false,
				"message", "An unexpected error occurred. Please try again later."
			));
		}
	}

	@PostMapping("/refunds/{id}/approve")
	public String approveRefund(@PathVariable Integer id, HttpSession session) {
		try {
			refundService.updateRefundStatus(id, "APPROVED");
			session.setAttribute("succMsg", "Refund request approved successfully");
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Failed to approve refund request: " + e.getMessage());
		}
		return "redirect:/admin/refunds";
	}

	@PostMapping("/refunds/{id}/reject")
	public String rejectRefund(@PathVariable Integer id, HttpSession session) {
		try {
			refundService.updateRefundStatus(id, "REJECTED");
			session.setAttribute("succMsg", "Refund request rejected successfully");
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Failed to reject refund request: " + e.getMessage());
		}
		return "redirect:/admin/refunds";
	}

	@PostMapping("/refunds/update-status")
	public String updateRefundStatus(@RequestParam Integer refundId, @RequestParam String status, HttpSession session) {
		try {
			refundService.updateRefundStatus(refundId, status);
			session.setAttribute("succMsg", "Refund status updated successfully to " + status);
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Failed to update refund status: " + e.getMessage());
		}
		return "redirect:/admin/refunds";
	}

}

package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.RazorpayService;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	@Autowired
	private RazorpayService razorpayService;

	@PostMapping("/create-order")
	public String createOrder(@RequestBody Map<String, Object> data) {
		int amount = (int) data.get("amount"); // ✅ Already in paise
		String currency = (String) data.get("currency");

		try {
			return razorpayService.createOrder(amount, currency, "recepient_100");
		} catch (RazorpayException e) {
			throw new RuntimeException(e);
		}
	}
}

package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Bean
    public RazorpayClient razorpayClient() throws Exception {
        return new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }
} 
package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.CustomerCartNotFoundException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.PaymentProcessException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.BillInfo;
import com.example.demo.model.OrderDetails;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/history")
	public ResponseEntity<?> getOrders() {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "success");
        responseBody.put("orders", orderService.getOrders());
        return ResponseEntity.ok(responseBody);
	}

    @GetMapping("/{orderId}")
	public ResponseEntity<?> getOrderById(@PathVariable("orderId") String orderId){
		Map<String, Object> responseBody = new HashMap<>();

		try {
			OrderDetails order = orderService.getOrder(orderId);
			responseBody.put("message", "success");
			responseBody.put("order", order);
			return ResponseEntity.ok(responseBody);		      
			
		} catch (OrderNotFoundException e) {
			responseBody.put("message", "order not found with id - " + orderId);
			return ResponseEntity.status(404).body(responseBody);
		}
        
        
	}

	@PostMapping("/{customerId}")
	public ResponseEntity<?> createOrder(@PathVariable("customerId") String customerId, @RequestBody(required = true) BillInfo billInfo){
		Map<String, Object> responseBody = new HashMap<>();
		try {
			OrderDetails order = orderService.createOrderForCustomer(customerId, billInfo);
			responseBody.put("message", "success");
			responseBody.put("newOrder", order);
			return ResponseEntity.ok(responseBody);	      
			
			// responseBody.put("message", "there was an error in customer order");
			// return ResponseEntity.status(400).body(responseBody);
		} catch (BadRequestException e) {
			responseBody.put("message", e.toString());
			return ResponseEntity.status(400).body(responseBody);
		} catch (PaymentProcessException e) {
			responseBody.put("message", "payment process has declined the request");
			return ResponseEntity.status(401).body(responseBody);
		} catch (CustomerCartNotFoundException e) {
			responseBody.put("message", "unable to find internal customer cart on server");
			return ResponseEntity.status(500).body(responseBody);	
		} catch (ProductNotFoundException e) {
			responseBody.put("message", "item(s) in internal cart doesn't exist in catalog of items");
			return ResponseEntity.status(500).body(responseBody);	
		}
	}

}

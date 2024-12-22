package com.example.demo.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.CustomerCartNotFoundException;
import com.example.demo.model.CustomerCart;
import com.example.demo.service.CartService;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerCart(@PathVariable("customerId") String customerId) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            CustomerCart cart = cartService.getCustomerCart(customerId);
            responseBody.put("message", "success");
            responseBody.put("cart", cartService.cartToJsonMapping(cart));
            return ResponseEntity.ok().body(responseBody);   
        } catch (CustomerCartNotFoundException e) {
            responseBody.put("message", "unable to find cart for customer with id - "+customerId);
            return ResponseEntity.status(404).body(responseBody);   
        }
 		
    }

    @PutMapping("/{customerId}/items")
    public ResponseEntity<?> updateCustomerCart(@PathVariable("customerId") String customerId, @RequestBody CustomerCart cart) {
        List<Map<String,Object>> failedEntries = cartService.updateCustomerCart(customerId, cart.getItems());
        Map<String, Object> responseBody = new HashMap<>();
        if (!failedEntries.isEmpty()) {
            responseBody.put("message", "The following items have failed. They are either less than 0 or the item does not have enough stock");
            responseBody.put("failedItems", failedEntries);
            return ResponseEntity.badRequest().body(responseBody);
        }
        responseBody.put("message", "success");
        return ResponseEntity.ok().body(responseBody);    		
    }
    

}

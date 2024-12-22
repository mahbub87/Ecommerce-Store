package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/im")
public class UserController {
    @Autowired
    private UserService userService;
        
    // GET /api/im/info/{CustomerID}
    @GetMapping("/info/{customerId}")
    public ResponseEntity<?> getCustomerInfo(@PathVariable("customerId") String customerId) {
    	// Fetch the customer from the ID GIVEN to us

        Map<String, Object> responseBody = new HashMap<>();
        try {
            return ResponseEntity.ok().body(userService.getCustomerById(customerId));   
        } catch (UserNotFoundException e) {
            responseBody.put("message", "customer not found with id - " + customerId);
            return ResponseEntity.status(404).body(responseBody);   
        }
    } 
    
    @PutMapping("/info/{customerId}")
    public ResponseEntity<?> updateCustomerInfo(@PathVariable("customerId") String customerId, @RequestBody User updatedUser) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            User savedUser = userService.updateUser(customerId, updatedUser);
            return ResponseEntity.ok(savedUser);
            
        } catch (UserNotFoundException e) {
            responseBody.put("message", "customer not found with id - " + customerId);
            return ResponseEntity.status(404).body(responseBody);   
        }
 
    }
    
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
    	
        List<User> users = userService.getUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Users retrieved successfully");
        response.put("users", users);

        return ResponseEntity.ok(response);
    }
    
}
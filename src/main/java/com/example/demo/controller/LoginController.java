package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Admin;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api")
public class LoginController {
	
	@Autowired
	private UserService userservice; 	

    // TODO Remove, not for actual deployment
    @PostMapping("/im/admin")
    public Admin addAdmin(@RequestBody Admin admin) {
        return userservice.addAdmin(admin);
    }
	
    @PostMapping("/im/signin")
    public ResponseEntity<?> checkUser(@RequestBody User loginRequest) {
    	System.out.println("Received Username " + loginRequest.getUsername());
    	System.out.println("Received Password " + loginRequest.getPassword());

        Map<String, Object> responseBody = new HashMap<>();
    	if (!userservice.checkValidUser(loginRequest.getUsername(), loginRequest.getPassword())) {
            responseBody.put("message", "Invalid Credentials");
            return ResponseEntity.status(401).body(responseBody);
    	}

        try {
            User user = userservice.getCustomerByUsername(loginRequest.getUsername());
            responseBody.put("message", "success");
            responseBody.put("userId", user.getUserId());
            return ResponseEntity.ok().body(responseBody);  
        } catch (UserNotFoundException e) {
            System.out.println("Bug: unable to find user despite logging in");
            responseBody.put("message", "unable to find user despite logging in");
            return ResponseEntity.status(500).body(responseBody);
        }
    }
    
    @PostMapping("/admincheck")
    public ResponseEntity<?> checkAdmin(@RequestBody User adminRequest) {
        System.out.println("Received Username: " + adminRequest.getUsername());
        System.out.println("Received Password: " + adminRequest.getPassword());

    	boolean admin_exists = userservice.validateAdmin(adminRequest.getUsername(), adminRequest.getPassword());
    	if (admin_exists) {
    		// thats good, and the user can sign in 
            return ResponseEntity.ok().body("{\"message\": \"Admin exists\"}");    		
    	}
    	else {
            return ResponseEntity.status(401).body("{\"message\": \"Invalid credentials- ADMIN\"}");

    	}
    }
    
    @PostMapping("/im/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {    	
    	// existing user is showiung up as true when checking the database.
        
        if (userservice.checkUsernameTaken(user.getUsername())) {
            // Username already taken
            return ResponseEntity.status(400).body("{\"message\": \"Username already exists\"}");
        }

        // Register the user to the database
        User newUser = userservice.registerNewUser(user);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "User registered successfully");
        responseBody.put("userId", newUser.getUserId());

        return ResponseEntity.ok(responseBody);    
    }

}
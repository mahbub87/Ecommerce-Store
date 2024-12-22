package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dao.repository.AdminRepository;
import com.example.demo.dao.repository.UserRepository;
import com.example.demo.dao.service.UserDAO;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Admin;
import com.example.demo.model.User;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserDAO userDAO;

    public User registerNewUser(User user) {
        User savedUser = userRepository.save(user);
        savedUser.getUserId();
        cartService.createCartForCustomer(savedUser.getUserId());
        return savedUser;
    }

	public boolean checkValidUser(String username, String password) {		
	    Optional<User> userCheck = userRepository.findByUsername(username);
        return userCheck.filter(user -> user.getPassword().equals(password)).isPresent();
	}

    public boolean checkUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User getCustomerByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) { return user; }

        // No user found
        throw new UserNotFoundException();
    }

    public User getCustomerById(String customerID) throws UserNotFoundException {
        // Fetch the customer from the database by ID
        User user = userRepository.findById(customerID).orElse(null);
        if (user != null) { return user; }

        // No user found
        throw new UserNotFoundException();
    }

	public List<User> getUsers() {
	    return userRepository.findAll();

	}
	public User updateUser(String customerId, User updatedUser) throws UserNotFoundException {
        // CHECKING if any fields need an update.
        Map<String, String> fields = new HashMap<>();
        if (updatedUser.getUsername() != null) {
            fields.put("username", updatedUser.getUsername());
        }
        if (updatedUser.getPassword() != null) {
            fields.put("password", updatedUser.getPassword());
        }
        if (updatedUser.getFirstName() != null) {
            fields.put("firstName", updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            fields.put("lastName", updatedUser.getLastName());
        }
        if (updatedUser.getEmail() != null) {
            fields.put("email", updatedUser.getEmail());
        }  
        if (updatedUser.getCreditCardNumber() != null) {
            fields.put("creditCardNumber", updatedUser.getCreditCardNumber());
        }
        if (updatedUser.getAddress() != null) {
            fields.put("address", updatedUser.getAddress());
        }
		return userDAO.updateCustomerInfo(customerId, fields);
	}
	
    public Admin addAdmin(@RequestBody Admin admin) {
        return adminRepository.save(admin);
    }
    
    
	public boolean validateAdmin(String adminUsername, String adminPassword) {
		
		Optional<Admin> admincheck = adminRepository.findByUsername(adminUsername);
		
        System.out.println("Admin found: " + admincheck.isPresent());
        
        return admincheck.filter(admin -> admin.getPassword().equals(adminPassword)).isPresent();
	}
}
package com.example.demo.ControllerTest;

import com.example.demo.controller.LoginController;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@SpringBootTest
class LoginControllerTest {

    @Autowired
    private LoginController loginController;

    @MockBean
    private UserService userService;

    @Test
    void testCheckUserSuccess() throws UserNotFoundException {
        User loginRequest = new User();
        loginRequest.setUserId("1");
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("password123");

        User user = new User();
        user.setUserId("1");
        user.setUsername("testUser");
        user.setPassword("password123");

        when(userService.checkValidUser(loginRequest.getUsername(), loginRequest.getPassword())).thenReturn(true);
        when(userService.getCustomerByUsername(loginRequest.getUsername())).thenReturn(user);

        ResponseEntity<?> response = loginController.checkUser(loginRequest);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));
        assertEquals(user.getUserId(), responseBody.get("userId"));

        verify(userService, times(1)).checkValidUser(loginRequest.getUsername(), loginRequest.getPassword());
        verify(userService, times(1)).getCustomerByUsername(loginRequest.getUsername());
    }

    @Test
    void testCheckUserInvalidCredentials() {
        User loginRequest = new User();
        loginRequest.setUserId("1");
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("wrongPassword");

        when(userService.checkValidUser(loginRequest.getUsername(), loginRequest.getPassword())).thenReturn(false);

        ResponseEntity<?> response = loginController.checkUser(loginRequest);

        assertEquals(ResponseEntity.status(401).build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid Credentials", responseBody.get("message"));

        verify(userService, times(1)).checkValidUser(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @Test
    void testCheckAdminSuccess() {
        User adminRequest = new User();
        adminRequest.setUserId("1");
        adminRequest.setUsername("adminUser");
        adminRequest.setPassword("password123");

        when(userService.validateAdmin(adminRequest.getUsername(),
                adminRequest.getPassword())).thenReturn(true);

        ResponseEntity<?> response = loginController.checkAdmin(adminRequest);

        assertEquals(ResponseEntity.ok().build().getStatusCode(),
                response.getStatusCode());
        assertEquals("{\"message\": \"Admin exists\"}", response.getBody());

        verify(userService, times(1)).validateAdmin(adminRequest.getUsername(),
                adminRequest.getPassword());
    }

    @Test
    void testCheckAdminInvalidCredentials() {
        User adminRequest = new User();
        adminRequest.setUserId("1");
        adminRequest.setUsername("adminUser");
        adminRequest.setPassword("wrongPassword");

        when(userService.validateAdmin(adminRequest.getUsername(),
                adminRequest.getPassword())).thenReturn(false);

        ResponseEntity<?> response = loginController.checkAdmin(adminRequest);

        assertEquals(ResponseEntity.status(401).build().getStatusCode(),
                response.getStatusCode());
        assertEquals("{\"message\": \"Invalid credentials- ADMIN\"}",
                response.getBody());

        verify(userService, times(1)).validateAdmin(adminRequest.getUsername(),
                adminRequest.getPassword());
    }

    @Test
    void testRegisterUserSuccess() {
        User user = new User();
        user.setUserId("1");
        user.setUsername("newUser");
        user.setPassword("password123");
        User newUser = new User();
        newUser.setUserId("1");
        newUser.setUsername("newUser");
        newUser.setPassword("password123");

        when(userService.checkUsernameTaken(user.getUsername())).thenReturn(false);
        when(userService.registerNewUser(user)).thenReturn(newUser);

        ResponseEntity<?> response = loginController.registerUser(user);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User registered successfully", responseBody.get("message"));
        assertEquals(newUser.getUserId(), responseBody.get("userId"));

        verify(userService, times(1)).checkUsernameTaken(user.getUsername());
        verify(userService, times(1)).registerNewUser(user);
    }

    @Test
    void testRegisterUserUsernameTaken() {
        User user = new User();
        user.setUserId("1");
        user.setUsername("existingUser");
        user.setPassword("password123");

        when(userService.checkUsernameTaken(user.getUsername())).thenReturn(true);

        ResponseEntity<?> response = loginController.registerUser(user);

        assertEquals(ResponseEntity.status(400).build().getStatusCode(), response.getStatusCode());
        assertEquals("{\"message\": \"Username already exists\"}", response.getBody());

        verify(userService, times(1)).checkUsernameTaken(user.getUsername());
    }
}

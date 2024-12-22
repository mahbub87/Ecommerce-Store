package com.example.demo.ControllerTest;

import com.example.demo.controller.UserController;
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
class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @Test
    void testGetCustomerInfoSuccess() throws UserNotFoundException {
        String customerId = "1";
        User user = new User();
        user.setUserId(customerId);
        user.setUsername("testUser");

        when(userService.getCustomerById(customerId)).thenReturn(user);

        ResponseEntity<?> response = userController.getCustomerInfo(customerId);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        User responseBody = (User) response.getBody();
        assertNotNull(responseBody);
        assertEquals(customerId, responseBody.getUserId());
        assertEquals("testUser", responseBody.getUsername());

        verify(userService, times(1)).getCustomerById(customerId);
    }

    @Test
    void testGetCustomerInfoNotFound() throws UserNotFoundException {
        String customerId = "1";

        when(userService.getCustomerById(customerId)).thenThrow(new UserNotFoundException());

        ResponseEntity<?> response = userController.getCustomerInfo(customerId);

        assertEquals(ResponseEntity.status(404).build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("customer not found with id - 1", responseBody.get("message"));

        verify(userService, times(1)).getCustomerById(customerId);
    }

    @Test
    void testUpdateCustomerInfoSuccess() throws UserNotFoundException {
        String customerId = "1";
        User updatedUser = new User();
        updatedUser.setUserId(customerId);
        updatedUser.setUsername("updatedUser");

        when(userService.updateUser(customerId, updatedUser)).thenReturn(updatedUser);

        ResponseEntity<?> response = userController.updateCustomerInfo(customerId, updatedUser);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        User responseBody = (User) response.getBody();
        assertNotNull(responseBody);
        assertEquals(customerId, responseBody.getUserId());
        assertEquals("updatedUser", responseBody.getUsername());

        verify(userService, times(1)).updateUser(customerId, updatedUser);
    }

    @Test
    void testUpdateCustomerInfoNotFound() throws UserNotFoundException {
        String customerId = "1";
        User updatedUser = new User();
        updatedUser.setUserId(customerId);
        updatedUser.setUsername("updatedUser");

        when(userService.updateUser(customerId, updatedUser)).thenThrow(new UserNotFoundException());

        ResponseEntity<?> response = userController.updateCustomerInfo(customerId, updatedUser);

        assertEquals(ResponseEntity.status(404).build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("customer not found with id - 1", responseBody.get("message"));

        verify(userService, times(1)).updateUser(customerId, updatedUser);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(
                new User() {
                    {
                        setUserId("1");
                        setUsername("user1");
                    }
                },
                new User() {
                    {
                        setUserId("2");
                        setUsername("user2");
                    }
                });

        when(userService.getUsers()).thenReturn(users);

        ResponseEntity<?> response = userController.getAllUsers();

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Users retrieved successfully", responseBody.get("message"));
        assertEquals(users, responseBody.get("users"));

        verify(userService, times(1)).getUsers();
    }
}

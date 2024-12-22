package com.example.demo.ControllerTest;

import com.example.demo.controller.CartController;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import com.example.demo.exception.CustomerCartNotFoundException;
import com.example.demo.model.CustomerCart;
import com.example.demo.service.CartService;

@SpringBootTest
class CartControllerTest {

    @Autowired
    private CartController cartController;

    @MockBean
    private CartService cartService;

    @Test
    void testGetCustomerCartSuccess() throws CustomerCartNotFoundException {
        String customerId = "12345";
        CustomerCart cart = new CustomerCart();
        Map<String, Object> cartJson = new HashMap<>();

        when(cartService.getCustomerCart(customerId)).thenReturn(cart);
        when(cartService.cartToJsonMapping(cart)).thenReturn(cartJson);

        ResponseEntity<?> response = cartController.getCustomerCart(customerId);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));
        assertEquals(cartJson, responseBody.get("cart"));

        verify(cartService, times(1)).getCustomerCart(customerId);
        verify(cartService, times(1)).cartToJsonMapping(cart);
    }

    @Test
    void testGetCustomerCartNotFound() throws CustomerCartNotFoundException {
        String customerId = "12345";

        when(cartService.getCustomerCart(customerId)).thenThrow(new CustomerCartNotFoundException());

        ResponseEntity<?> response = cartController.getCustomerCart(customerId);

        assertEquals(ResponseEntity.status(404).build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("unable to find cart for customer with id - 12345", responseBody.get("message"));

        verify(cartService, times(1)).getCustomerCart(customerId);
    }

    @Test
    void testUpdateCustomerCartSuccess() {
        String customerId = "12345";
        CustomerCart cart = new CustomerCart();
        cart.setItems(new ArrayList<>());

        when(cartService.updateCustomerCart(customerId, cart.getItems())).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = cartController.updateCustomerCart(customerId, cart);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));

        verify(cartService, times(1)).updateCustomerCart(customerId, cart.getItems());
    }

    @Test
    void testUpdateCustomerCartWithFailedItems() {
        String customerId = "12345";
        CustomerCart cart = new CustomerCart();
        cart.setItems(new ArrayList<>());

        List<Map<String, Object>> failedItems = new ArrayList<>();
        Map<String, Object> failedItem = new HashMap<>();
        failedItem.put("item", "exampleItem");
        failedItems.add(failedItem);

        when(cartService.updateCustomerCart(customerId, cart.getItems())).thenReturn(failedItems);

        ResponseEntity<?> response = cartController.updateCustomerCart(customerId, cart);

        assertEquals(ResponseEntity.badRequest().build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(
                "The following items have failed. They are either less than 0 or the item does not have enough stock",
                responseBody.get("message"));
        assertEquals(failedItems, responseBody.get("failedItems"));

        verify(cartService, times(1)).updateCustomerCart(customerId, cart.getItems());
    }
}
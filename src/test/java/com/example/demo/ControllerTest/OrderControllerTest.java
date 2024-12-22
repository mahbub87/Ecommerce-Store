package com.example.demo.ControllerTest;

import com.example.demo.controller.OrderController;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import com.example.demo.exception.*;
import com.example.demo.model.BillInfo;
import com.example.demo.model.OrderDetails;
import com.example.demo.service.OrderService;

@SpringBootTest
class OrderControllerTest {

    @Autowired
    private OrderController orderController;

    @MockBean
    private OrderService orderService;

    @Test
    void testGetOrdersSuccess() {
        List<OrderDetails> orders = Arrays.asList(new OrderDetails() {
            {
                setOrderId("1");
                setCustomerId("customer1");
                setItems(new ArrayList<>());
                setTotal(100.0);
            }
        });
        when(orderService.getOrders()).thenReturn(orders);

        ResponseEntity<?> response = orderController.getOrders();

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));
        assertEquals(orders, responseBody.get("orders"));

        verify(orderService, times(1)).getOrders();
    }

    @Test
    void testGetOrderByIdSuccess() throws OrderNotFoundException {
        String orderId = "1";
        OrderDetails order = new OrderDetails() {
            {
                setOrderId(orderId);
                setCustomerId("customer1");
                setItems(new ArrayList<>());
                setTotal(100.0);
            }
        };
        when(orderService.getOrder(orderId)).thenReturn(order);

        ResponseEntity<?> response = orderController.getOrderById(orderId);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));
        assertEquals(order, responseBody.get("order"));

        verify(orderService, times(1)).getOrder(orderId);
    }

    @Test
    void testGetOrderByIdNotFound() throws OrderNotFoundException {
        String orderId = "1";
        when(orderService.getOrder(orderId)).thenThrow(new OrderNotFoundException());

        ResponseEntity<?> response = orderController.getOrderById(orderId);

        assertEquals(ResponseEntity.status(404).build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("order not found with id - 1", responseBody.get("message"));

        verify(orderService, times(1)).getOrder(orderId);
    }

    @Test
    void testCreateOrderSuccess() throws BadRequestException, PaymentProcessException, CustomerCartNotFoundException,
            ProductNotFoundException {
        String customerId = "1";
        BillInfo billInfo = new BillInfo();
        OrderDetails newOrder = new OrderDetails() {
            {
                setOrderId("1");
                setCustomerId(customerId);
                setItems(new ArrayList<>());
                setTotal(100.0);
            }
        };

        when(orderService.createOrderForCustomer(customerId, billInfo)).thenReturn(newOrder);

        ResponseEntity<?> response = orderController.createOrder(customerId, billInfo);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));
        assertEquals(newOrder, responseBody.get("newOrder"));

        verify(orderService, times(1)).createOrderForCustomer(customerId, billInfo);
    }

    @Test
    void testCreateOrderBadRequest() throws BadRequestException, PaymentProcessException, CustomerCartNotFoundException,
            ProductNotFoundException {
        String customerId = "1";
        BillInfo billInfo = new BillInfo();

        when(orderService.createOrderForCustomer(customerId, billInfo))
                .thenThrow(new BadRequestException("Invalid request"));

        ResponseEntity<?> response = orderController.createOrder(customerId, billInfo);

        assertEquals(ResponseEntity.status(400).build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.get("message").toString().contains("Invalid request"));

        verify(orderService, times(1)).createOrderForCustomer(customerId, billInfo);
    }

    @Test
    void testCreateOrderPaymentProcessException() throws BadRequestException, PaymentProcessException,
            CustomerCartNotFoundException, ProductNotFoundException {
        String customerId = "1";
        BillInfo billInfo = new BillInfo();

        when(orderService.createOrderForCustomer(customerId, billInfo)).thenThrow(new PaymentProcessException());

        ResponseEntity<?> response = orderController.createOrder(customerId, billInfo);

        assertEquals(ResponseEntity.status(401).build().getStatusCode(), response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("payment process has declined the request", responseBody.get("message"));

        verify(orderService, times(1)).createOrderForCustomer(customerId, billInfo);
    }
}
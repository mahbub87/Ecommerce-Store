package com.example.demo.ControllerTest;

import com.example.demo.controller.CatalogController;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.Item;
import com.example.demo.dao.repository.ItemRepository;
import com.example.demo.service.CatalogService;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.*;

@SpringBootTest
class CatalogControllerTest {

    @Autowired
    private CatalogController catalogController;

    @MockBean
    private CatalogService catalogService;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void testGetAllItems() {
        String brand = "BrandA";
        String category = "CategoryA";
        Map<String, String> filters = new HashMap<>();
        filters.put("brand", brand);
        filters.put("category", category);

        Item item = new Item();
        item.setItemId("1");
        item.setName("Item1");
        item.setBrand(brand);
        item.setCategory(category);
        item.setQuantity(10);
        item.setPrice(100.0);

        List<Item> mockItems = Arrays.asList(item);

        when(catalogService.getCatalogItems(filters)).thenReturn(mockItems);

        ResponseEntity<?> response = catalogController.getAllItems(brand, category);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));
        assertEquals(mockItems, responseBody.get("items"));

        verify(catalogService, times(1)).getCatalogItems(filters);
    }

    @Test
    void testGetProductSuccess() throws ProductNotFoundException {
        String itemId = "1";

        Item mockItem = new Item();
        mockItem.setItemId(itemId);
        mockItem.setName("Item1");
        mockItem.setBrand("BrandA");
        mockItem.setCategory("CategoryA");
        mockItem.setQuantity(10);
        mockItem.setPrice(100.0);

        when(catalogService.getItemById(itemId)).thenReturn(mockItem);

        ResponseEntity<?> response = catalogController.getProduct(itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));
        assertEquals(mockItem, responseBody.get("item"));

        verify(catalogService, times(1)).getItemById(itemId);
    }

    @Test
    void testGetProductNotFound() throws ProductNotFoundException {
        String itemId = "1";

        when(catalogService.getItemById(itemId)).thenThrow(new ProductNotFoundException());

        ResponseEntity<?> response = catalogController.getProduct(itemId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("no product found with id - 1", responseBody.get("message"));

        verify(catalogService, times(1)).getItemById(itemId);
    }

    @Test
    void testUpdateProductInventorySuccess() throws ProductNotFoundException {
        String itemId = "1";

        Item mockItem = new Item();
        mockItem.setItemId(itemId);
        mockItem.setName("Item1");
        mockItem.setBrand("BrandA");
        mockItem.setCategory("CategoryA");
        mockItem.setQuantity(10);
        mockItem.setPrice(100.0);
        int qty = 5;

        when(catalogService.updateItemQuantity(itemId, qty)).thenReturn(mockItem);

        ResponseEntity<?> response = catalogController.updateProductInventory(itemId, qty);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));

        verify(catalogService, times(1)).updateItemQuantity(itemId, qty);
    }

    @Test
    void testUpdateProductInventoryNotFound() throws ProductNotFoundException {
        String itemId = "1";
        int qty = 5;

        doThrow(new ProductNotFoundException()).when(catalogService).updateItemQuantity(itemId, qty);

        ResponseEntity<?> response = catalogController.updateProductInventory(itemId, qty);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("no product found with id - 1", responseBody.get("message"));

        verify(catalogService, times(1)).updateItemQuantity(itemId, qty);
    }

    @Test
    void testAddItem() {
        Item item = new Item();
        item.setItemId("1");
        item.setName("NewItem");
        item.setBrand("BrandB");
        item.setCategory("CategoryB");
        item.setQuantity(20);
        item.setPrice(200.0);

        when(itemRepository.save(item)).thenReturn(item);

        ResponseEntity<?> response = catalogController.addItem(item);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("message"));
        assertEquals(item, responseBody.get("item"));

        verify(itemRepository, times(1)).save(item);
    }
}

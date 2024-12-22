package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.example.demo.dao.repository.CartRepository;
import com.example.demo.dao.service.CartDAO;
import com.example.demo.exception.CustomerCartNotFoundException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.CustomerCart;
import com.example.demo.model.ItemEntry;
import com.example.demo.model.Item;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CartDAO cartDAO;

    /**
     * Creates customer cart given cutomer ID.
     * If the customer already has associated cart, a cart is not created
     */
    public void createCartForCustomer(String customerId) {
        if (cartRepository.findByCustomerId(customerId).isPresent()) {return ;}

        CustomerCart cart = new CustomerCart();
        cart.setItems(new ArrayList<ItemEntry>());
        cart.setCustomerId(customerId);
        cartRepository.save(cart);
    }

    public CustomerCart getCustomerCart(String customerId) throws CustomerCartNotFoundException{
        CustomerCart cart = cartRepository.findByCustomerId(customerId).orElse(null);
        if (cart != null) { return cart; }

        // No cart found
        throw new CustomerCartNotFoundException();
    }

    /**
     * Updates customer cart given cutomer ID and list of new item quantities.
     * If there are no issues with updating the shopping cart, returns empty list.
     * If there is not enough stock or attempting to set a negative value, returns 
     * list of item ids with problematic quantities (succesful cart entries are still
     * updated).
     */
    public List<Map<String, Object>> updateCustomerCart(String customerId, List<ItemEntry> newValues) {
        CustomerCart cart = cartRepository.findByCustomerId(customerId).get();
        List<ItemEntry> items = cart.getItems();
        List<Map<String, Object>> failedItems = new ArrayList<Map<String, Object>>();
        
        for (ItemEntry updateEntry: newValues) {
            try {
                String itemId = updateEntry.getItemId();
                Item catalogItem = catalogService.getItemById(itemId);
                int newQty = updateEntry.getQty();
                int inStockQty = catalogItem.getQuantity();
                // Check if newQty is valid
                if (newQty < 0 || newQty > inStockQty) {                
                    failedItems.add(cartItemToJsonMapping(updateEntry));
                    continue;
                }

                // Update qty in items
                boolean found = false;
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getItemId().equals(itemId)) {
                        found = true;
                        if (newQty == 0) {
                            items.remove(i);
                        }
                        else {
                            items.get(i).setQty(newQty);
                        }
                        break;
                    }
                }

                // If not in items, add to items
                if (!found && newQty > 0) {
                    ItemEntry newEntry = new ItemEntry();
                    newEntry.setItemId(itemId);
                    newEntry.setQty(newQty);
                    items.add(newEntry);
                }
            } catch(ProductNotFoundException e) {
                System.out.println("Bug: Encountered a product in customer cart with no matching id in catalog");
            }            
        }

        cartDAO.updateCustomerCart(customerId, items);
        return failedItems;

    }

    public void clearCustomerCart(String customerId) {
        cartDAO.clearCustomerCart(customerId);
    }
    
    // Utility Methods
    
    public Map<String, Object> cartToJsonMapping(CustomerCart cart) {
        Map<String, Object> cartMap = new HashMap<>();
        cartMap.put("customerId", cart.getCustomerId());
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (ItemEntry entry: cart.getItems()) {
            itemList.add(cartItemToJsonMapping(entry));
        }
        cartMap.put("items", itemList);
        return cartMap;
    }

    public Map<String, Object> cartItemToJsonMapping(ItemEntry item) {
        
        Map<String, Object> mappedEntry = new HashMap<>();
        mappedEntry.put("itemId", item.getItemId());
        mappedEntry.put("qty", item.getQty());
        return mappedEntry;
    }
    

}

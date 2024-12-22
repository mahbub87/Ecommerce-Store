package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.dao.repository.OrdersRepository;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.CustomerCartNotFoundException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.PaymentProcessException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.BillInfo;
import com.example.demo.model.CustomerCart;
import com.example.demo.model.ItemEntry;
import com.example.demo.model.OrderDetails;
import com.example.demo.model.Item;

@Service
public class OrderService {
    
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private CatalogService catalogService;

    public OrderDetails createOrderForCustomer(String customerId, BillInfo billInfo) 
    throws CustomerCartNotFoundException, BadRequestException, PaymentProcessException, ProductNotFoundException{
        CustomerCart cart = cartService.getCustomerCart(customerId);

        if (cart.getItems().isEmpty()) {
            System.out.println("Attempted to checkout an empty cart");
            throw new BadRequestException("attempted to checkout an empty cart");
        }

        List<String> itemIds = new ArrayList<>();
        for (ItemEntry itemEntry: cart.getItems()) {
            itemIds.add(itemEntry.getItemId());
        }
        List<Item> currentStock = catalogService.getItemsByIds(itemIds);

        if (currentStock.isEmpty()) { 
            System.out.println("item(s) in internal cart doesn't exist in catalog of items");
            throw new ProductNotFoundException(); 
        }
        
        List<ItemEntry> orderEntries = new ArrayList<>();

        // Validate customer order to see if there is enough stock for each item in cart
        boolean invalidCart = false;
        for (ItemEntry cartEntry: cart.getItems()) {
            for (Item itemStock: currentStock) {
                if (itemStock.getItemId().equals(cartEntry.getItemId())) {
                    if (itemStock.getQuantity() < cartEntry.getQty()) {
                        // Cart entry has more items than stock, update cart to match inventory
                        invalidCart = true;
                        cartEntry.setQty(itemStock.getQuantity());
                    } else {
                        // Valid cart entry, add to order
                        orderEntries.add(cartEntry);
                    }
                    break;
                }
            }
        }

        if (invalidCart) {
            System.out.println("Customer cart was invalid");
            cartService.updateCustomerCart(customerId, cart.getItems());
            throw new BadRequestException("customer cart had more items than what was in stock, cart quantities have been changed internally");
        }

        // Pseudo payment processor
        if (billInfo.getCreditCardCVV().contains("2")) {
            System.out.println("payment has failed");
            throw new PaymentProcessException();
        }        
        
        System.out.println("Valid cart and payment process complete");


        // Update catalog
        double total = 0.0;
        for (ItemEntry cartEntry: cart.getItems()) {
            for (Item itemStock: currentStock) {
                if (itemStock.getItemId().equals(cartEntry.getItemId())) {
                    total += cartEntry.getQty() * itemStock.getPrice();
                    int newStockQty = itemStock.getQuantity() - cartEntry.getQty();
                    try {
                        catalogService.updateItemQuantity(itemStock.getItemId(), newStockQty);
                    } catch (ProductNotFoundException e) {
                        System.out.println("Bug: Product could not be found after loading from catalog");
                    }
                    break;
                }
            }
        }

        cartService.clearCustomerCart(customerId);

        // Create new order
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomerId(customerId);
        orderDetails.setItems(orderEntries);
        total *= 1.13;
        orderDetails.setTotal(total);
        orderDetails.setBillInfo(billInfo);
        orderDetails.setDate(new Date());

        return ordersRepository.save(orderDetails);
    }

	public List<OrderDetails> getOrders() {
		return ordersRepository.findAll(); 
	}

    public OrderDetails getOrder(String orderId) throws OrderNotFoundException{
        OrderDetails order = ordersRepository.findById(orderId).orElse(null);
        if (order != null) { return order; }

        // No order found
        throw new OrderNotFoundException();
    }

}

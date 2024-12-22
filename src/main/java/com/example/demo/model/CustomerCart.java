package com.example.demo.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;

@Document
public class CustomerCart {
    @Id
    private String cartId;
    @Field("customerId")
    private String customerId;
    @Field("items")
    private List<ItemEntry> items;
    
    public String getCartId() {
        return cartId;
    }

    public List<ItemEntry> getItems() {
        return items;
    }
    public void setItems(List<ItemEntry> items) {
        this.items = items;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}

package com.example.demo.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class ItemEntry {
    @Field("itemId")
    private String itemId;
    @Field("qty")
    private int qty;

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public int getQty() {
        return qty;
    }
    public void setQty(int qty) {
        this.qty = qty;
    }
    
}
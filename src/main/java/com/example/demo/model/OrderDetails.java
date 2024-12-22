package com.example.demo.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

@Document
public class OrderDetails {
    @Id
    private String orderId;
    @Field("customerId")
    private String customerId;
    @Field("total") 
    private double total;
    @Field("date")
    private Date date;
    @Field("billInfo")
    private BillInfo billInfo;
    @Field("items")
    private List<ItemEntry> items;

    public List<ItemEntry> getItems() {
        return items;
    }
    public void setItems(List<ItemEntry> items) {
        this.items = items;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public BillInfo getBillInfo() {
        return billInfo;
    }
    public void setBillInfo(BillInfo billInfo) {
        this.billInfo = billInfo;
    }
}

package com.example.demo.dao.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.demo.model.ItemEntry;

@Service
public class CartDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void clearCustomerCart(String customerId) {
        Query query = new Query(Criteria.where("customerId").is(customerId));
        Update update = new Update().set("items", new ArrayList<ItemEntry>());
        mongoTemplate.updateFirst(query, update, "customerCart");
    }

    public void updateCustomerCart(String customerId, List<ItemEntry> items) {
        Query query = new Query(Criteria.where("customerId").is(customerId));
        Update update = new Update().set("items", items);
        mongoTemplate.updateFirst(query, update, "customerCart");
    }

}

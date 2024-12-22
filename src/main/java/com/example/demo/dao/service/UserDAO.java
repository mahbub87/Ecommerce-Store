package com.example.demo.dao.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.demo.dao.repository.UserRepository;
import com.example.demo.model.User;

@Service
public class UserDAO {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepository userRepository;

    public User updateCustomerInfo(String customerId, Map<String, String> updates) {
        Query query = new Query(Criteria.where("_id").is(customerId));
        Update update = new Update();
        for (String field: updates.keySet()) {
            update = update.set(field, updates.get(field));
        }
        mongoTemplate.updateFirst(query, update, "user");
        return userRepository.findById(customerId).orElse(new User());
    }

}

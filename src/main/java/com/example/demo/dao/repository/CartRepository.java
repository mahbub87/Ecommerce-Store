package com.example.demo.dao.repository;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.CustomerCart;

@Repository
public interface CartRepository extends MongoRepository<CustomerCart, String>{
    Optional<CustomerCart> findByCustomerId(String customerId);
}

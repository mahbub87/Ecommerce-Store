package com.example.demo.dao.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.OrderDetails;

@Repository
public interface OrdersRepository extends MongoRepository<OrderDetails, String>{
    Optional<OrderDetails> findById(String orderId);
}

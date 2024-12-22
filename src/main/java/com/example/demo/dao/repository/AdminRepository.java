package com.example.demo.dao.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Admin;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

	Optional<Admin> findByUsername(String username);
	
	// Add the sht u wanna look for in the database
	
	

}
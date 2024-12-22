package com.example.demo.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;

@Document
public class Admin {
	
	@Id
	private String id;
	@Field("username")
	private String username; 
	@Field("password")
	private String password;
	@Field("email")
	private String email;
	
	
	public Admin() {
		
	}
	public Admin(String admin_id, String username, String password, String email) {
		super();
		this.id = admin_id;
		this.username = username;
		this.password = password;
		this.email = email;
	}
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	
	

}
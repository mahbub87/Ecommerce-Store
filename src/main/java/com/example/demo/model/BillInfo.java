package com.example.demo.model;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

public class BillInfo {
    @Field("address")
    private String address;
    @Field("firstName")
    private String firstName;
    @Field("lastName")
    private String lastName;
    @Field("phoneNumber")
    private String phoneNumber;
    @Field("creditCardNum")
    private String creditCardNum;
    @Transient
    private String creditCardCVV;
    @Transient
    private String creditCardExpiry;
    @Field("email")
    private String email;

    // Getters and Setters
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getCreditCardNum() {
        return creditCardNum;
    }
    public void setCreditCardNum(String creditCardNum) {
        this.creditCardNum = creditCardNum;
    }
    public String getCreditCardCVV() {
        return creditCardCVV;
    }
    public void setCreditCardCVV(String creditCardCVV) {
        this.creditCardCVV = creditCardCVV;
    }
    public String getCreditCardExpiry() {
        return creditCardExpiry;
    }
    public void setCreditCardExpiry(String creditCardExpiry) {
        this.creditCardExpiry = creditCardExpiry;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }    
}

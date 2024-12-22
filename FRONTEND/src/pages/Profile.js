import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchCustomerInfo, updateCustomerInfo } from "../services/api"; 


const Profile = () => {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    firstName: "",
    lastName: "",
    creditCardNumber: "",
    address: "",
    email: "",
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  // Fetch current customer info from the API
  useEffect(() => {
    if(localStorage.getItem("customerId") === null){
        navigate('/login');
    }
    const loadCustInfo = async () => {
      try {
        const customerId = localStorage.getItem("customerId");
        const custData = await fetchCustomerInfo(customerId);
        console.log("Loaded customer data", custData); // Debugging
        setFormData(prevData => ({
          ...prevData,
          "username": custData["username"],
          "password": custData["password"],
          "firstName": custData["firstName"],
          "lastName": custData["lastName"],
          "creditCardNumber": custData["creditCardNumber"],
          "address": custData["address"],
          "email": custData["email"],
        }));
      } catch (error) {
        console.error("Failed to fetch customer data:", error);
      }
    };
    loadCustInfo();
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Basic validations
    if (!validateEmail(formData.email)) {
      setError('Invalid email format!');
      return;
    }
    if (!/^\d{16}$/.test(formData.creditCardNumber)) {
      setError('Credit Card number must be a 16-digit number!');
      return;
    }

    setError(""); // Clear previous errors

    try {
      let currData = {...formData};
      for (let key in currData) {
        if (currData[key] === '') {
          currData[key] = null;
        }
      }
      const customerId = localStorage.getItem("customerId");
      const response = await updateCustomerInfo(customerId, currData);
      console.log("Succesful update:", response);
      navigate("/");
    } catch (err) {
      console.error("Login error:", err);
      setError(err.message || "An error occurred while updating.");
    }
  };

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSignOut = () => {
    localStorage.removeItem("customerId");
    localStorage.setItem("cart", "[]");
    navigate("/"); 
  }

  return (
    
    <div className="login-page">
        <p>&nbsp;</p>
        <p>&nbsp;</p>
      <div className="wrapper">
        <form onSubmit={handleSubmit}>
          <h1>Profile & Update</h1>
          User
          <div className="input-box">
            <input
              type="text"
              name="username"
              placeholder="Username"
              value={formData.username}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-user"></i>
          </div>
          Password
          <div className="input-box">
            <input
              type="text"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-user"></i>
          </div>
          First Name
          <div className="input-box">
            <input
              type="text"
              name="firstName"
              placeholder="First Name"
              value={formData.firstName}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-user"></i>
          </div>
          Last Name
          <div className="input-box">
            <input
              type="text"
              name="lastName"
              placeholder="Last Name"
              value={formData.lastName}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-user"></i>
          </div>
          Credit Card Number
          <div className="input-box">
            <input
              type="text"
              name="creditCardNumber"
              placeholder="Credit Card Number"
              value={formData.creditCardNumber}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-user"></i>
          </div>
          Address
          <div className="input-box">
            <input
              type="text"
              name="address"
              placeholder="Address"
              value={formData.address}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-user"></i>
          </div>
          Email
          <div className="input-box">
            <input
              type="text"
              name="email"
              placeholder="Email"
              value={formData.email}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-user"></i>
          </div>

          {error && <p className="error">{error}</p>}

          <button type="submit" className="btn">
            Update
          </button>
          <p>&nbsp;</p>
        </form>
        <button type="submit" className="btn" onClick={handleSignOut}>
            Sign Out
        </button>
      </div>
    </div>
  );
};

export default Profile;
import React, { useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { CartContext } from "../CartContext";
import { createOrder, fetchCustomerInfo } from "../services/api"; 


const CheckoutView = () => {
  const [formData, setFormData] = useState({
    address: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
    creditCardNum: '',
    creditCardCVV: '',
    creditCardExpiry: '',
    email: '',
  });

  const {clearCart } = useContext(CartContext)
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();
  const customerId = localStorage.getItem("customerId"); 

  // Fetch current customer info from the API
  useEffect(() => {

    if(localStorage.getItem("customerId")==null){
        navigate('/login');
    }
    const loadCustInfo = async () => {
      try {
        const custData = await fetchCustomerInfo(customerId);
        console.log("Loaded customer data", custData); // Debugging

        setFormData(prevData => ({
          ...prevData,
          "firstName": custData["firstName"],
          "lastName": custData["lastName"],
          "creditCardNum": custData["creditCardNumber"],
          "address": custData["address"],
          "email": custData["email"]
        }));
      } catch (error) {
        console.error("Failed to fetch customer data:", error);
      }
    };
    loadCustInfo();
  }, [customerId,navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Basic validations
    if (!formData.firstName || !formData.lastName || !formData.address) {
      setError('All fields are required!');
      return;
    }
    if (!validateEmail(formData.email)) {
      setError('Invalid email format!');
      return;
    }
    if (!/^\d{16}$/.test(formData.creditCardNum)) {
      setError('Credit Card number must be a 16-digit number!');
      return;
    }
    if (!/^\d{3}$/.test(formData.creditCardCVV)) {
      setError('Credit Card CVV must be a 3-digit number!');
      return;
    }
    if (!/^\d{2}\/\d{2}$/.test(formData.creditCardExpiry)) {
      setError('Credit Card expiry must follow XX/XX format!');
      return;
    }
    if (!/^\d{10}$/.test(formData.phoneNumber)) {
      setError('Phone number must be a 10-digit number!');
      return;
    }

    setError('');
    setSuccess('');

    try {

      // Checkout user
      const response = await createOrder(customerId, formData);

      if (response.error) {
        throw new Error(response.message || 'Registration failed! Please try again.');
      }
      clearCart();
      setSuccess('Checkout successful! Redirecting...');
      setTimeout(() => navigate('/'), 2000); // Redirect after 2 seconds
    } catch (err) {
      setError(err.message);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  return (
    <div className="login-page">
      <div className="wrapper">
        <form onSubmit={handleSubmit}>
          <h1>Checkout</h1>

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

          <div className="input-box">
            <input
              type="text"
              name="firstName"
              placeholder="First Name"
              value={formData.firstName}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-lock"></i>
          </div>

          <div className="input-box">
            <input
              type="text"
              name="lastName"
              placeholder="Last Name"
              value={formData.lastName}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-lock"></i>
          </div>

          <div className="input-box">
            <input
              type="text"
              name="phoneNumber"
              placeholder="Phone Number"
              value={formData.phoneNumber}
              onChange={handleChange}
              required
            />
          </div>

          <div className="input-box">
            <input
              type="text"
              name="creditCardNum"
              placeholder="Credit Card Number"
              value={formData.creditCardNum}
              onChange={handleChange}
              required
            />
          </div>

          <div className="input-box">
            <input
              type="password"
              name="creditCardCVV"
              placeholder="Credit Card CVV (XXX)"
              value={formData.creditCardCVV}
              onChange={handleChange}
              required
            />
          </div>

          <div className="input-box">
            <input
              type="text"
              name="creditCardExpiry"
              placeholder="Credit Card Expiry (XX/XX)"
              value={formData.creditCardExpiry}
              onChange={handleChange}
              required
            />
          </div>

          <div className="input-box">
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>

          {error && <p className="error">{error}</p>}
          {success && <p className="success">{success}</p>}

          <button type="submit" className="btn">
            Checkout
          </button>
        </form>
      </div>
    </div>
  );
};

export default CheckoutView;

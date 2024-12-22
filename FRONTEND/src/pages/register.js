import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CartContext } from "../CartContext";
import { registerUser, updateCartItems } from '../services/api'; // Adjust the import path based on your project structure

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    creditCardId: '',
    address: '',
    email: '',
  });
  const { cart } = useContext(CartContext);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Basic validations
    if (!formData.username || !formData.password || !formData.confirmPassword) {
      setError('All fields are required!');
      return;
    }
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match!');
      return;
    }
    if (!validateEmail(formData.email)) {
      setError('Invalid email format!');
      return;
    }
    if (!/^\d{16}$/.test(formData.creditCardId)) {
      setError('Credit Card ID must be a 16-digit number!');
      return;
    }

    setError('');
    setSuccess('');

    try {
      const response = await registerUser(formData);

      if (response.error) {
        throw new Error(response.message || 'Registration failed! Please try again.');
      }
      
      const requestItems = [];
      for (const requestItem of cart) {
        requestItems.push({"itemId": requestItem["itemId"], "qty": requestItem["quantity"]});
      }
      const updatedCart = await updateCartItems(response.userId, requestItems);
      
      

      setSuccess('Registration successful! Redirecting...');
      setTimeout(() => navigate('/'), 2000);
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
          <h1>Register</h1>

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

          <div className="input-box">
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-lock"></i>
          </div>

          <div className="input-box">
            <input
              type="password"
              name="confirmPassword"
              placeholder="Confirm Password"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
            />
            <i className="bx bxs-lock"></i>
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
          </div>

          <div className="input-box">
            <input
              type="text"
              name="creditCardId"
              placeholder="Credit Card ID"
              value={formData.creditCardId}
              onChange={handleChange}
              required
            />
          </div>

          <div className="input-box">
            <input
              type="text"
              name="address"
              placeholder="Address"
              value={formData.address}
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
            Register
          </button>
        </form>
      </div>
    </div>
  );
};

export default Register;
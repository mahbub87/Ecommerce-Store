import React, { useState} from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../services/api"; 

const Login = () => {
  
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();
  if(localStorage.getItem("customerId")!=null){
    navigate('/profile')
  }
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.username || !formData.password) {
      setError("Username and Password are required!");
      return;
    }

    setError(""); 

    try {
      const response = await login(formData.username, formData.password);
      console.log("Login successful:", response);
  
      const  customerId  = response.userId;
      console.log(customerId);
      localStorage.setItem("customerId", customerId); 
      
      navigate("/"); 
    } catch (err) {
      console.error("Login error:", err);
      setError(err.message || "An error occurred while logging in.");
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  return (
    <div className="login-page">
      <div className="wrapper">
        <form onSubmit={handleSubmit}>
          <h1>Login</h1>

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

          {error && <p className="error">{error}</p>}

          <button type="submit" className="btn">
            Login
          </button>
          <p>&nbsp;</p>
          <p style={{ textAlign: "center" }}>
            Don't have an account?{" "}
            <Link
              to="/register"
              style={{ color: "white", textDecoration: "underline" }}
            >
              Register
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default Login;

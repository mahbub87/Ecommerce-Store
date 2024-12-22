import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { CartContext } from '../CartContext';

const Navbar = () => {
  const { cart } = useContext(CartContext);

  const getCartItemCount = () =>
    Array.isArray(cart) ? cart.reduce((total, item) => total + (item.quantity || 0), 0) : 0;


    
  return (
    <nav className="navbar">
      <h1>SMMB Electronics</h1>
      <ul>
        <li><Link to="/admin">Admin</Link></li>
        <li><Link to="/login">Sign In</Link></li> 
         <li><Link to="/profile">Profile</Link></li>
        <li><Link to="/">Catalog</Link></li>
        <li>
          <Link to="/cart">Cart ({getCartItemCount()})</Link>
        </li>
      </ul>
    </nav>
  );
};

export default Navbar;
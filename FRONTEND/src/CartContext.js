import React, { createContext, useState, useEffect } from "react";
import { fetchCart, updateCartItems } from "./services/api"; // Import API functions

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initializeCart = async () => {
      const customerId = localStorage.getItem("customerId");
      if (!customerId) {
        console.error("Customer ID not found");
        setCart([]);
        setIsLoading(false);
        return;
      }
  
      try {
        const backendCartResponse = await fetchCart(customerId);
        const backendCart = backendCartResponse.cart.items;
        if (backendCart.length > 0) {
          setCart(backendCart); 
          localStorage.setItem("cart", JSON.stringify(backendCart)); 
        } else {
          const localCart = JSON.parse(localStorage.getItem("cart")) || [];
          setCart(localCart); 
          if (localCart.length > 0) {
            await updateCartItems(customerId, localCart); 
          }
        }
      } catch (error) {
        console.error("Error fetching or initializing cart:", error);
      } finally {
        setIsLoading(false);
      }
    };
  
    initializeCart();
  }, []);

  useEffect(() => {
    if (!isLoading) {
      localStorage.setItem("cart", JSON.stringify(cart)); 
    }
  }, [cart, isLoading]);

  const syncCartWithBackend = async (updatedCart) => {
    const customerId = localStorage.getItem("customerId");
    if (!customerId) {
      console.error("Customer ID not found");
      return;
    }
  
    const backendCart = {
      items: updatedCart.map((item) => ({
        itemId: item.itemId,
        qty: item.quantity, 
      })),
    };
  
    try {
      await updateCartItems(customerId, backendCart.items);
    } catch (error) {
      console.error("Error updating cart in backend:", error);
    }
  };


  const addToCart = (product) => {
    setCart((prevCart) => {
      const existingItem = prevCart.find((item) => item.itemId === product.itemId);
      let updatedCart;
  
      if (existingItem) {
        updatedCart = prevCart.map((item) =>
          item.itemId === product.itemId
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      } else {
        updatedCart = [...prevCart, { ...product, quantity: 1 }];
      }
  
      syncCartWithBackend(updatedCart); 
      return updatedCart;
    });
  };

  // Remove item from cart
  const removeFromCart = (productId) => {
    setCart((prevCart) => {
      const updatedCart = prevCart.filter((item) => item.itemId !== productId);
      syncCartWithBackend(updatedCart);
      return updatedCart;
    });
  };
  const clearCart = () => {
    setCart([]);
  }
  // Update item quantity
  const updateQuantity = (productId, quantity) => {
    const qty = Math.max(1, parseInt(quantity) || 1);
    setCart((prevCart) => {
      const updatedCart = prevCart.map((item) =>
        item.itemId === productId ? { ...item, quantity: qty } : item
      );
      syncCartWithBackend(updatedCart); 
      return updatedCart;
    });
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <CartContext.Provider
      value={{ cart, clearCart, addToCart, removeFromCart, updateQuantity }}
    >
      {children}
    </CartContext.Provider>
  );
};

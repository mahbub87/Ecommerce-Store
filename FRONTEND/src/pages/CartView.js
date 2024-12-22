import React, { useContext } from "react";
import { CartContext } from "../CartContext";
import { useNavigate } from "react-router-dom";

const CartView = () => {
  const { cart, removeFromCart, updateQuantity } = useContext(CartContext);
  const navigate = useNavigate();

  const calculateTotal = () =>
    cart.reduce((total, item) => total + item.price * item.quantity, 0);

  const handleCheckout = () => {
    console.log("Cart data for checkout:", cart);
    
    navigate("/checkout");
  };

  return (
    <div className="cart-view">
      <h2>Your Shopping Cart</h2><p>&nbsp;</p>
      {cart.length === 0 ? (
        <p>Your cart is empty. Go back to the catalog to add items.</p>
      ) : (
        <div>
          {cart.map((item) => (
            <div key={item.id} className="cart-item">
             <div className="cont">
                <img src={item.imageUrl} alt={item.name} />
             </div>
              <div className="cont2">
                <p>&nbsp;</p>
                <h3>{item.name}</h3><p>&nbsp;</p>
                <p>Price: ${item.price}</p><p>&nbsp;</p>
                <p>
                  Quantity:{" "}
                  <input
                    type="number"
                    value={item.quantity}
                    min="1"
                    onChange={(e) => updateQuantity(item.itemId, e.target.value)}
                  />
                </p><p>&nbsp;</p>
                <button onClick={() => removeFromCart(item.itemId)}>Remove</button>
              </div>
            </div>
          ))}
          <h3>Total: ${(calculateTotal()*1.13).toFixed(2)}</h3>
          <button onClick={handleCheckout}>Proceed to Checkout</button>
        </div>
      )}
    </div>
  );
};

export default CartView;

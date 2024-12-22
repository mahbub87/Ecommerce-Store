import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { CartContext } from '../CartContext';

const CatalogItem = ({ product }) => {
  const { addToCart } = useContext(CartContext);
  const navigate = useNavigate();

  const handleViewDetails = () => {
    console.log("Navigating to product:", product); // Debug
    navigate(`/product/${product.itemId}`);
  };


  return (
    <div className="catalog-item" onClick={handleViewDetails}>
      <div className="image-container">
        <img src={product.imageUrl} alt={product.name} />
      </div>

      <div className="desc">
        <p>{product.brand}</p><p>&nbsp;</p>
        <h3>{product.name}</h3><p>&nbsp;</p>
        <p>{product.description}</p><p>&nbsp;</p>
        <p>Price: ${product.price}</p><p>&nbsp;</p>
        <button 
          onClick={(e) => {
            e.stopPropagation(); 
            addToCart(product);
          }}
        >
          Add to Cart
        </button>
      </div>
    </div>
  );
};

export default CatalogItem;

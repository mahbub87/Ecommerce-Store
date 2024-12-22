import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { fetchProductById } from '../services/api'; 

const ProductDetails = () => {
  const { productId } = useParams();
  const [product, setProduct] = useState(null);


  useEffect(() => {
    const fetchProductDetails = async () => {
      try {
        const productData = await fetchProductById(productId);
        console.log("Fetched product:", productData);
        setProduct(productData.item);
      } catch (error) {
        console.error("Error fetching product details:", error.message);
      }
    };
  
    fetchProductDetails();
  }, [productId]);

  return product ? (
    <div className="catalog-item2">
      <div className="image-container2">
        <img src={product.imageUrl} alt={product.name} />
      </div>
      <div className="desc">
        <h1>{product.brand} {product.name}</h1>
        <p><strong>{product.description}</strong></p>
        <p><strong>${product.price}</strong></p>
        <p>{product.detailed}</p>
      </div>
    </div>
  ) : (
    <p>Product not found</p>
  );
};

export default ProductDetails;

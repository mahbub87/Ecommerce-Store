import React, { useState, useEffect } from "react";
import CatalogItem from "../components/CatalogItem";
import { fetchCatalog } from "../services/api"; 

const CatalogView = () => {
  const [products, setProducts] = useState([]); 
  const [filterType, setFilterType] = useState("All");
  const [filterValue, setFilterValue] = useState("");
  const [loading, setLoading] = useState(true);


  useEffect(() => {
    const loadCatalog = async () => {
      try {
        setLoading(true);
        const data = await fetchCatalog();
        console.log("Loaded products:", data); 
        setProducts(Array.isArray(data) ? data : []); 
      } catch (error) {
        console.error("Failed to load products:", error);
        setProducts([]); 
      } finally {
        setLoading(false);
      }
    };

    loadCatalog();
  }, []);


  const uniqueBrands = [...new Set(products.map((product) => product.brand))];
  const uniqueCategories = [...new Set(products.map((product) => product.category))];

 
  const filteredProducts = products.filter((product) => {
    if (filterType === "All") return true;
    return product[filterType.toLowerCase()] === filterValue;
  });

  return (
    <div className="catalog-view">
      <h2>Products</h2>

      {/* Loading Indicator */}
      {loading ? (
        <p>Loading products...</p>
      ) : (
        <>
          {/* Filter Controls */}
          <div className="filter-controls">
            <select
              value={filterType}
              onChange={(e) => {
                setFilterType(e.target.value);
                setFilterValue("");
              }}
            >
              <option value="All">All</option>
              <option value="Brand">Brand</option>
              <option value="Category">Category</option>
            </select>

            {filterType !== "All" && (
              <select
                value={filterValue}
                onChange={(e) => setFilterValue(e.target.value)}
              >
                <option value="">Select {filterType}</option>
                {(filterType === "Brand" ? uniqueBrands : uniqueCategories).map((option) => (
                  <option key={option} value={option}>
                    {option}
                  </option>
                ))}
              </select>
            )}
          </div>

          {/* Product Grid */}
          <div className="product-grid">
            {filteredProducts.length > 0 ? (
              filteredProducts.map((product) => (
                <CatalogItem key={product.id} product={product} />
              ))
            ) : (
              <p>No products match your criteria.</p>
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default CatalogView;

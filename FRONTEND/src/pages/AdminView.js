import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchOrderHistory, fetchOrderById, fetchProductById, fetchCatalog, updateProductQuantity } from '../services/api';

const SalesHistoryPage = () => {
  const [orders, setOrders] = useState([]);
  const [sortedOrders, setSortedOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [productDetails, setProductDetails] = useState({});
  const [sortConfig, setSortConfig] = useState({ key: 'date', direction: 'asc' });
  const [catalog, setCatalog] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await fetchOrderHistory();
        setOrders(data.orders || []);
        setSortedOrders(data.orders || []);
      } catch (err) {
        setError('Failed to fetch order history.');
      } finally {
        setLoading(false);
      }
    };

    const fetchCatalogData = async () => {
      try {
        const catalogData = await fetchCatalog();
        setCatalog(catalogData);
      } catch (err) {
        console.error('Failed to fetch catalog data:', err);
      }
    };

    fetchData();
    fetchCatalogData();
  }, []);

  const fetchProductDetails = async (itemId) => {
    if (productDetails[itemId]) return productDetails[itemId]; 

    try {
      const data = await fetchProductById(itemId);
      setProductDetails((prev) => ({ ...prev, [itemId]: data.item }));
      return data.item;
    } catch (err) {
      console.error(`Failed to fetch product details for item ${itemId}`);
      return null;
    }
  };

  const handleOrderClick = async (orderId) => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchOrderById(orderId);
      const enrichedItems = await Promise.all(
        data.order.items.map(async (item) => {
          const product = await fetchProductDetails(item.itemId);
          return {
            ...item,
            name: product?.name || 'N/A',
            price: product?.price || 'N/A',
          };
        })
      );
      setSelectedOrder({ ...data.order, items: enrichedItems });
    } catch (err) {
      setError('Failed to fetch order details.');
    } finally {
      setLoading(false);
    }
  };

  const handleSort = (key) => {
    const direction = sortConfig.key === key && sortConfig.direction === 'asc' ? 'desc' : 'asc';
    setSortConfig({ key, direction });

    const sorted = [...orders].sort((a, b) => {
      let valA, valB;
      if (key === 'customer') {
        valA = `${a.billInfo?.firstName || ''} ${a.billInfo?.lastName || ''}`.toLowerCase();
        valB = `${b.billInfo?.firstName || ''} ${b.billInfo?.lastName || ''}`.toLowerCase();
      } else {
        valA = key === 'date' ? new Date(a[key]) : a[key];
        valB = key === 'date' ? new Date(b[key]) : b[key];
      }

      if (valA < valB) return direction === 'asc' ? -1 : 1;
      if (valA > valB) return direction === 'asc' ? 1 : -1;
      return 0;
    });

    setSortedOrders(sorted);
  };

  const handleCustomerClick = (customerId) => {
    localStorage.setItem('customerId', customerId);
    console.log(customerId);
    navigate('/profile');
  };

   const handleQuantityChange = async (itemId, newQuantity) => {
    try {
      await updateProductQuantity(itemId, newQuantity);
      setCatalog((prevCatalog) =>
        prevCatalog.map((item) =>
          item.itemId === itemId ? { ...item, quantity: newQuantity } : item
        )
      );
      console.log(`Updated item ${itemId} to quantity ${newQuantity}`);
    } catch (err) {
      console.error(`Failed to update quantity for item ${itemId}:`, err);
    }
  };

  return (
    <div style={{ color: 'white' }}>
      <p> &nbsp;</p>
      {loading ? (
        <p>Loading...</p>
      ) : error ? (
        <p>{error}</p>
      ) : (
        <div style={{ display: 'flex', gap: '2rem' }}>
          <div>
            <h2>Order List</h2><p> &nbsp;</p>
            <table border="1">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th onClick={() => handleSort('customer')}>Customer Name</th>
                  <th>Total</th>
                  <th onClick={() => handleSort('date')}>Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {sortedOrders.map((order) => (
                  <tr key={order.orderId}>
                    <td>{order.orderId}</td>
                    <td>
                      <a href="#" onClick={() => handleCustomerClick(order?.customerId)}>
                        {`${order.billInfo?.firstName || 'N/A'} ${order.billInfo?.lastName || 'N/A'}`}
                      </a>
                    </td>
                    <td>${order.total?.toFixed(2) || '0.00'}</td>
                    <td>{order.date ? new Date(order.date).toLocaleString() : 'N/A'}</td>
                    <td>
                      <button onClick={() => handleOrderClick(order.orderId)}>View Details</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
                  
            {selectedOrder && ( 
              <div><p> &nbsp;</p>
                <h2>Order Details</h2><p> &nbsp;</p>
                <p><strong>Order ID:</strong> {selectedOrder.orderId}</p>
                <p><strong>Customer:</strong> {`${selectedOrder.billInfo.firstName} ${selectedOrder.billInfo.lastName}`}</p>
                <p><strong>Total:</strong> ${selectedOrder.total.toFixed(2)}</p>
                <p><strong>Date:</strong> {new Date(selectedOrder.date).toLocaleString()}</p>
                <h3>Items:  </h3>
                <table border="1">
                  <thead>
                    <tr>
                      <th>ProductID</th>
                      <th>Name</th>
                      <th>Price</th>
                      <th>Quantity</th>
                    </tr>
                  </thead>
                  <tbody>
                    {selectedOrder.items.map((item) => (
                      <tr key={item.itemId}>
                        <td>{item.itemId}</td>
                        <td>{item.name}</td>
                        <td>${item.price}</td>
                        <td>{item.qty}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            <p> &nbsp;</p>
            <h2>Product Catalog</h2>
            <p> &nbsp;</p>
            <table border="1">
              <thead>
                <tr>
                  <th>Product ID</th>
                  <th>Name</th>
                  <th>Quantity</th>
                </tr>
              </thead>
              <tbody>
                {catalog.map((product) => (
                  <tr key={product.itemId}>
                    <td>{product.itemId}</td>
                    <td>{product.name}</td>
                    <td>
                    <input
                        type="number"
                        value={product.quantity}
                        onChange={(e) => handleQuantityChange(product.itemId, parseInt(e.target.value))}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default SalesHistoryPage;

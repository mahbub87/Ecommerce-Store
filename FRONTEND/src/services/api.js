let API_URL;
if (window.location.hostname === "localhost") {
  API_URL = "http://localhost:8080/api";
} else {
  API_URL = "https://" + window.location.hostname + "/api";
}
console.log(API_URL);

// ----------------------------AUTHENTICATION----------------------------------------
// Login 
export const login = async (username, password) => {
  const response = await fetch(`${API_URL}/im/signin`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || "Login failed");
  }
  return response.json();
};
// Fetch all users
export const getAllUsers = async () => {
  try {
    const response = await fetch(`${API_URL}/im/users`, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Failed to fetch users");
    }

    const data = await response.json();
    return data.users; // Return the users from the response
  } catch (error) {
    console.error("Error fetching users:", error);
    throw error; // Rethrow the error for the calling component to handle
  }
};
// Admin Login
export const loginAdmin = async (username, password) => {
  const response = await fetch(`${API_URL}/admincheck`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || "Admin login failed");
  }
  return response.json();
};

// ----------------------------CATALOG SERVICE----------------------------------------
// Fetch all products in catalog
export const fetchCatalog = async () => {
  try {
    const response = await fetch(`${API_URL}/catalog`);
    const data = await response.json();
    console.log("Fetched catalog data:", data); // Log response for debugging
    return Array.isArray(data.items) ? data.items : []; // Ensure data is an array
  } catch (error) {
    console.error("Error fetching catalog:", error);
    return []; // Return an empty array on error
  }
};

// Fetch product details by ID
export const fetchProductById = async (itemID) => {
  const response = await fetch(`${API_URL}/catalog/${itemID}`);
  if (!response.ok) {
    throw new Error('Failed to fetch product');
  }
  return await response.json();
};

// Update product quantity
export const updateProductQuantity = async (itemId, newQuantity) => {
  const response = await fetch(`${API_URL}/catalog/${itemId}/quantity?qty=${newQuantity}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
  });
  return response.json();
};

// ----------------------------ORDERING SERVICE----------------------------------------
// Fetch all sales orders with optional filters
export const fetchOrderHistory = async (filters = {}) => {
  const query = new URLSearchParams(filters).toString();
  const response = await fetch(`${API_URL}/order/history?${query}`);
  return response.json();
};

// Fetch order details by Order ID
export const fetchOrderById = async (orderId) => {
  const response = await fetch(`${API_URL}/order/${orderId}`);
  return response.json();
};

// Fetch orders associated with a Customer ID
export const fetchOrdersByCustomer = async (customerId) => {
  const response = await fetch(`${API_URL}/order/${customerId}`);
  return response.json();
};

// Create a new order for a customer
export const createOrder = async (customerId, billData) => {
  const response = await fetch(`${API_URL}/order/${customerId}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(billData),
  });

  if (!response.ok) {
    const error = await response.json();
    if (response.status === 400) {
      throw new Error(`Invalid order: ${JSON.stringify(error.invalidItems)}`);
    }
    if (response.status === 401) {
      throw new Error("Payment authorization failed");
    }
  }
  return response.json();
};

// ----------------------------IDENTITY MANAGEMENT----------------------------------------
// Fetch customer info by ID
export const fetchCustomerInfo = async (customerId) => {
  const response = await fetch(`${API_URL}/im/info/${customerId}`);
  return response.json();
};

// Update customer info
export const updateCustomerInfo = async (customerId, updatedData) => {
  const response = await fetch(`${API_URL}/im/info/${customerId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(updatedData),
  });
  return response.json();
};

// Register new user
export const registerUser = async (customerData) => {
  const response = await fetch(`${API_URL}/im/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(customerData),
  });
  return response.json();
};

// ----------------------------SHOPPING CART----------------------------------------

// Get cart items for a customer
export const fetchCart = async (customerId) => {
  const response = await fetch(`${API_URL}/cart/${customerId}`);
  if (!response.ok) {
    throw new Error(`Failed to fetch cart for customer ${customerId}`);
  }
  return response.json();
};

// Update items in the cart
export const updateCartItems = async (customerId, items) => {
  const response = await fetch(`${API_URL}/cart/${customerId}/items`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ items }),
  });
  if (!response.ok) {
    throw new Error(`Failed to update cart for customer ${customerId}`);
  }
  return response.json();
};

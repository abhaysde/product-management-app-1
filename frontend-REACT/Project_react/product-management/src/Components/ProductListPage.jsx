import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./ProductListPage.css";
import Logo from "./Logo";

const API_BASE = "http://localhost:8080/api";

const ProductListPage = () => {
  const navigate = useNavigate();

  const [products, setProducts] = useState([]);
  const [showForm, setShowForm] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    quantity: "",
    price: "",
    discountPrice: "",
    isAvailable: true,
    imageFile: null,
    imageUrl: "",
  });

  const [filter, setFilter] = useState("all");
  const [searchQuery, setSearchQuery] = useState("");

  const [editingProductId, setEditingProductId] = useState(null);

  // Fetch products on mount
  useEffect(() => {
    fetch(API_BASE, { credentials: "include" })
      .then((res) => res.json())
      .then((data) => setProducts(data))
      .catch((err) => console.error("Error fetching products:", err));
  }, []);

  // Handle input changes including image file
  const handleChange = (e) => {
    const { name, value, type, checked, files } = e.target;

    if (type === "file" && files && files.length > 0) {
      const file = files[0];
      setFormData((prev) => ({
        ...prev,
        imageFile: file,
        imageUrl: URL.createObjectURL(file),
      }));
    } else if (type === "checkbox") {
      setFormData((prev) => ({
        ...prev,
        [name]: checked,
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  // Handle form submit (both create and edit)
  const handleSubmit = async (e) => {
    e.preventDefault();

    const productPayload = {
      name: formData.name.trim(),
      quantity: parseInt(formData.quantity, 10),
      price: parseFloat(formData.price),
      discountPrice: formData.discountPrice
        ? parseFloat(formData.discountPrice)
        : null,
      isAvailable: formData.isAvailable,
    };

    try {
      let res;

      if (editingProductId) {
        // Edit product (PUT JSON only)
        res = await fetch(`${API_BASE}/${editingProductId}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(productPayload),
          credentials: "include",
        });
      } else {
        // Create product (POST with FormData)
        const formDataToSend = new FormData();
        formDataToSend.append(
          "product",
          new Blob([JSON.stringify(productPayload)], { type: "application/json" })
        );

        if (formData.imageFile) {
          formDataToSend.append("image", formData.imageFile);
        }

        res = await fetch(`${API_BASE}/product`, {
          method: "POST",
          body: formDataToSend,
          credentials: "include",
        });
      }

      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(`Failed to save product: ${errorText || res.status}`);
      }

      const savedProduct = await res.json();

      if (editingProductId) {
        setProducts((prev) =>
          prev.map((p) => (p.id === editingProductId ? savedProduct : p))
        );
      } else {
        setProducts((prev) => [savedProduct, ...prev]);
      }

      // Reset form and states
      setFormData({
        name: "",
        quantity: "",
        price: "",
        discountPrice: "",
        isAvailable: true,
        imageFile: null,
        imageUrl: "",
      });
      setEditingProductId(null);
      setShowForm(false);
    } catch (error) {
      console.error("Save product error:", error);
      alert("Error saving product. See console for details.");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this product?")) return;

    try {
      const res = await fetch(`${API_BASE}/${id}`, {
        method: "DELETE",
        credentials: "include",
      });

      if (res.status === 204) {
        setProducts((prev) => prev.filter((product) => product.id !== id));
      } else {
        const errorText = await res.text();
        throw new Error(`Failed to delete product: ${errorText || res.status}`);
      }
    } catch (error) {
      console.error("Delete product error:", error);
      alert("Failed to delete product.");
    }
  };

  const handleLogout = async () => {
  try {
    const res = await fetch(`http://localhost:8080/auth/logout`, {
      method: "GET",
      credentials: "include", // Include cookies in the request
    });

    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(`Logout failed: ${errorText}`);
    }

    // Redirect to login page
    navigate("/login");
  } catch (error) {
    console.error("Logout error:", error);
    alert("Failed to logout. Please try again.");
  }
};



  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleEditClick = (product) => {
    setEditingProductId(product.id);
    setFormData({
      name: product.name,
      quantity: product.quantity.toString(),
      price: product.price.toString(),
      discountPrice: product.discountPrice ? product.discountPrice.toString() : "",
      isAvailable: product.isAvailable,
      imageFile: null,
      imageUrl: product.imageUrl || "",
    });
    setShowForm(true);
  };

  // Filter and search products
  const filteredProducts = products
    .filter((product) => {
      if (filter === "all") {
        return product.flag !== 1; // hide deleted
      }
      if (filter === "unavailable") {
        return product.deleted === 1 || !product.isAvailable;
      }
      return true;
    })
    .filter((product) =>
      product.name.toLowerCase().includes(searchQuery.toLowerCase())
    );

  return (
    <div className="product-list-container">
      <Logo />

      <nav className="nav-bar">
        <div className="search-container">
          <i className="search-icon fas fa-search"></i>
          <input
            type="text"
            className="search-input"
            placeholder="Search products..."
            value={searchQuery}
            onChange={handleSearchChange}
          />
          <button className="search-btn" onClick={() => {}}>
            Search
          </button>
        </div>

        <div className="nav-left">
          <button
            className="nav-btn"
            onClick={() => {
              setShowForm(!showForm);
              setFilter("all");
              setEditingProductId(null);
              setFormData({
                name: "",
                quantity: "",
                price: "",
                discountPrice: "",
                isAvailable: true,
                imageFile: null,
                imageUrl: "",
              });
            }}
          >
            {showForm ? "Cancel" : "➕ Create Product"}
          </button>

          <button
            className="nav-btn"
            onClick={() => {
              setFilter("all");
              setShowForm(false);
            }}
          >
            Home
          </button>

          <button className="nav-btn logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </nav>

      <h2 className="product-list-heading">🛒 Product List</h2>

      {showForm && (
        <div className="create-product-form-wrapper">
          <div className="create-product-form">
            <h3>{editingProductId ? "Edit Product" : "Add New Product"}</h3>
            <form onSubmit={handleSubmit}>
              <div>
                <label htmlFor="name">Product Name</label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  placeholder="Product Name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                />
              </div>

              <div>
                <label htmlFor="quantity">Quantity</label>
                <input
                  type="number"
                  id="quantity"
                  name="quantity"
                  placeholder="Quantity"
                  value={formData.quantity}
                  onChange={handleChange}
                  required
                  min="0"
                />
              </div>

              <div>
                <label htmlFor="price">Price</label>
                <input
                  type="number"
                  id="price"
                  name="price"
                  placeholder="Price"
                  value={formData.price}
                  onChange={handleChange}
                  required
                  step="0.01"
                  min="0"
                />
              </div>

              <div>
                <label htmlFor="discountPrice">Discount Price</label>
                <input
                  type="number"
                  id="discountPrice"
                  name="discountPrice"
                  placeholder="Discount Price (optional)"
                  value={formData.discountPrice}
                  onChange={handleChange}
                  step="0.01"
                  min="0"
                />
              </div>

              <div>
                <label htmlFor="imageUrl">Product Image</label>
                <input
                  type="file"
                  id="imageUrl"
                  name="imageUrl"
                  accept="image/*"
                  onChange={handleChange}
                />
                {formData.imageUrl && (
                  <img
                    src={formData.imageUrl}
                    alt="Preview"
                    className="image-preview"
                  />
                )}
              </div>

              <div className="checkbox-container">
                <label htmlFor="isAvailable" className="checkbox-label">
                  <input
                    type="checkbox"
                    id="isAvailable"
                    name="isAvailable"
                    checked={formData.isAvailable}
                    onChange={handleChange}
                  />
                  Available
                </label>
              </div>

              <button type="submit">
                {editingProductId ? "Save Changes" : "Add Product"}
              </button>
            </form>
          </div>
        </div>
      )}

      <div className="product-list-grid">
        {filteredProducts.length > 0 ? (
          filteredProducts.map((product) => (
            <div
              key={product.id}
              className={`product-card ${
                product.deleted ? "deleted" : ""
              }`}
            >
              {product.imageUrl ? (
                <img
                  src={product.imageUrl}
                  alt={product.name}
                  className="product-image"
                />
              ) : (
                <div className="product-image-placeholder">No Image</div>
              )}

              <div className="product-details">
                <h3 className="product-title">{product.name}</h3>
                <p className="product-price">Price: ₹{product.price.toFixed(2)}</p>
                {product.discountPrice != null && (
                  <p className="discount">
                    Discount: ₹{product.discountPrice.toFixed(2)}
                  </p>
                )}
                <p>Quantity: {product.quantity}</p>
                <p
                  className={`status ${
                    product.deleted || !product.isAvailable
                      ? "out-of-stock"
                      : "in-stock"
                  }`}
                >
                  {product.deleted
                    ? "❌ Out of Stock"
                    : "✅ In Stock"}
                </p>
                <div className="action-buttons">
                  <button
                    className="edit-btn"
                    onClick={() => handleEditClick(product)}
                  >
                    Edit
                  </button>
                  <button
                    className="delete-btn"
                    onClick={() => handleDelete(product.id)}
                  >
                    🗑 Delete
                  </button>
                </div>
              </div>
            </div>
          ))
        ) : (
          <p className="no-products-message">No products to display.</p>
        )}
      </div>
    </div>
  );
};

export default ProductListPage;
  
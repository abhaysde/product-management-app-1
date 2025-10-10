import React, { useState } from 'react';
import './LoginPage.css';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useNavigate } from 'react-router-dom';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [name, setName] = useState('');
  const [isSignup, setIsSignup] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

  const API_BASE = 'http://localhost:8083/auth';
  const navigate = useNavigate();

  const resetForm = () => {
    setUsername('');
    setPassword('');
    setConfirmPassword('');
    setName('');
    setErrorMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');

    if (isSignup) {
      if (!name.trim()) {
        setErrorMessage('Full name is required');
        return;
      }

      if (password !== confirmPassword) {
        setErrorMessage('Passwords do not match');
        return;
      }

      const signupPayload = {
        username: username.trim(),
        password,
        name: name.trim(),
      };

      try {
        const response = await fetch(`${API_BASE}/signup`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(signupPayload),
        });

        if (response.ok) {
          toast.success('Signup successful! Please log in.');
          resetForm();
          setIsSignup(false);
        } else {
          const text = await response.text();
          toast.error(`Signup failed: ${text}`);
        }
      } catch (error) {
        console.error('Signup Error:', error);
        toast.error('Signup request failed.');
      }
    } else {
      const loginPayload = {
        username: username.trim(),
        password,
      };

      try {
        const response = await fetch(`${API_BASE}/login`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(loginPayload),
        });

        if (response.ok) {
          const data = await response.json();
          localStorage.setItem('token', data.token);
          toast.success('Login successful! Redirecting...');

          setTimeout(() => navigate('/productlist'), 1000);
        } else {
          const text = await response.text();

          if (text.includes('User not found')) {
            toast.warn('Account not found. Please create an account.');
            setIsSignup(true);
          } else {
            toast.error(`Login failed: ${text}`);
          }
        }
      } catch (error) {
        console.error('Login Error:', error);
        toast.error('Login request failed.');
      }
    }
  };

  return (
    <div className="login-container">
      <ToastContainer position="top-right" autoClose={2000} />

      <nav className="navbar">
        <div className="logo">
          <h1>𝙨𝙝𝙤𝙥𝙞𝙛𝙮</h1>
        </div>
      </nav>

      <div className="login-form">
        <h2>{isSignup ? 'Create Account' : 'Sign In'}</h2>
        {errorMessage && <div className="error-message">{errorMessage}</div>}

        <form onSubmit={handleSubmit}>
          {isSignup && (
            <div className="input-group">
              <label htmlFor="name">Full Name</label>
              <input
                type="text"
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
          )}

          <div className="input-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="input-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {isSignup && (
            <div className="input-group">
              <label htmlFor="confirmPassword">Confirm Password</label>
              <input
                type="password"
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>
          )}

          <button type="submit" className="login-btn">
            {isSignup ? 'Create Account' : 'Sign In'}
          </button>

          <div className="footer-links">
            <button
              type="button"
              onClick={() => {
                setIsSignup(!isSignup);
                setErrorMessage('');
              }}
              className="link-button"
            >
              {isSignup
                ? 'Already have an account? Sign In'
                : "Don't have an account? Create Account"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;

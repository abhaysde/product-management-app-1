import React from 'react';
import './Logo.css'; 

const Logo = ({ name }) => {
  if (!name) return null;

  const initial = name.charAt(0).toUpperCase();

  return (
    <div className="user-avatar">
      <div className="avatar-circle">{initial}</div>
      <span className="user-name">{name}</span>
    </div>
  );
};

export default Logo;

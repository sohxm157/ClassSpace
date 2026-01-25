import { Link } from "react-router-dom";
import "../styles/dashboard.css";

function Navbar() {
  return (
    <div className="navbar">
      <button className="logout-btn">Logout</button>

      <div className="nav-center">
        <span className="brand">ClassSpace</span>
        <span className="divider">|</span>
        <span className="college">College Name</span>
      </div>

      <Link to="/profile" className="profile-link">
        <img
          src="https://via.placeholder.com/40"
          className="profile-pic"
          alt="profile"
        />
        <span className="username">ABC XYZ</span>
      </Link>
    </div>
  );
}

export default Navbar;

import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/dashboard.css";

function Navbar() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/auth/me", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Not logged in");
        return res.json();
      })
      .then((data) => setUser(data))
      .catch(() => navigate("/login"));
  }, [navigate]);

  const logout = async () => {
    await fetch("http://localhost:8080/auth/logout", {
      method: "POST",
      credentials: "include",
    });
    navigate("/login");
  };

  if (!user) return null;

  return (
    <div className="navbar">
      {/* LEFT */}
      <div className="nav-left">
        <span className="brand">ClassSpace</span>
        <span className="divider">|</span>
        <span className="college">
          {user.className || "Not Joined"}
        </span>
      </div>

      {/* RIGHT */}
      <div className="nav-right">
        <button className="logout-btn" onClick={logout}>
          Logout
        </button>

        <Link to="/student/profile" className="profile-box">
          <div className="avatar">
            {user.name.charAt(0).toUpperCase()}
          </div>
          <span className="username">{user.name}</span>
        </Link>
      </div>
    </div>
  );
}

export default Navbar;

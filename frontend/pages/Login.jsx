import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/login.css";

const Login = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: "",
    password: "",
    role: "STUDENT",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    if (!form.username || !form.password) {
      setError("All fields are required");
      setLoading(false);
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({
          username: form.username, // PRN or Email
          password: form.password,
          role: form.role,
        }),
      });

      const message = await response.text();

      if (response.status === 200) {
        if (message.includes("TEACHER")) {
          navigate("/teacher/dashboard");
        } else {
          navigate("/student/dashboard");
        }
      } else if (response.status === 428) {
        navigate("/change-password");
      } else {
        setError(message || "Login failed");
      }
    } catch {
      setError("Backend not reachable");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">

        {/* 🔥 TOP RIGHT REGISTER */}
        <div className="register-top">
          <span onClick={() => navigate("/register")}>
            Register Here!
          </span>
        </div>

        <div className="login-header">
          <div className="logo">📅</div>
          <h1>ClassSpace</h1>
          <p>Your Smart Timetable & Learning Platform</p>
        </div>

        {error && <p className="error">{error}</p>}

        <form onSubmit={handleSubmit}>
          <label>Username</label>
          <input
            type="text"
            name="username"
            placeholder="PRN / Email"
            value={form.username}
            onChange={handleChange}
          />

          <label>Password</label>
          <input
            type="password"
            name="password"
            placeholder="Enter your password"
            value={form.password}
            onChange={handleChange}
          />

          <label className="login-as">Login As</label>
          <div className="role-box">
            <button
              type="button"
              className={form.role === "STUDENT" ? "active" : ""}
              onClick={() => setForm({ ...form, role: "STUDENT" })}
            >
              Student
            </button>

            <button
              type="button"
              className={form.role === "TEACHER" ? "active" : ""}
              onClick={() => setForm({ ...form, role: "TEACHER" })}
            >
              Teacher
            </button>
          </div>

          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? "Signing In..." : "Sign In"}
          </button>
        </form>

        <p className="forgot" onClick={() => navigate("/forgot-password")}>
          Forgot password?
        </p>
      </div>
    </div>
  );
};

export default Login;

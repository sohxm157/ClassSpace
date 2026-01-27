import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "../styles/change-password.css";

const ChangePassword = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const fromForgot = Boolean(location.state?.email);

  const [form, setForm] = useState({
    email: location.state?.email || "",
    newPassword: "",
    confirmPassword: "",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.newPassword || !form.confirmPassword) {
      setError("All fields are required");
      return;
    }

    if (form.newPassword !== form.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    try {
      const res = await fetch(
        "http://localhost:8080/auth/change-password",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({
            email: form.email,
            newPassword: form.newPassword,
          }),
        }
      );

      const msg = await res.text();

      if (res.status === 200) {
        setSuccess("Password changed successfully. Please login again.");
        setTimeout(() => navigate("/login"), 1500);
      } else {
        setError(msg);
      }
    } catch {
      setError("Backend not reachable");
    }
  };

  return (
    <div className="cp-wrapper">
      <div className="cp-card">
        <h2>Change Password</h2>

        {error && <p className="error">{error}</p>}
        {success && <p className="success">{success}</p>}

        <form onSubmit={handleSubmit}>
          {/* Email only for forgot-password flow */}
          {fromForgot && (
            <>
              <label>Email</label>
              <input
                type="email"
                name="email"
                value={form.email}
                disabled
              />
            </>
          )}

          <label>New Password</label>
          <input
            type="password"
            name="newPassword"
            value={form.newPassword}
            onChange={handleChange}
          />

          <label>Confirm Password</label>
          <input
            type="password"
            name="confirmPassword"
            value={form.confirmPassword}
            onChange={handleChange}
          />

          <button type="submit">Update Password</button>
        </form>
      </div>
    </div>
  );
};

export default ChangePassword;

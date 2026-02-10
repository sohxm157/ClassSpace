import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/forgot-password.css";

const ForgotPassword = () => {
  const navigate = useNavigate();

  const [step, setStep] = useState("EMAIL"); // EMAIL | OTP
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // 🔥 EMAIL VALIDATION
  const isValidEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const sendOtp = async () => {
    if (!email) {
      setError("Email is required");
      return;
    }

    if (!isValidEmail(email)) {
      setError("Enter a valid email address");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const res = await fetch(
        "http://localhost:8080/auth/forgot-password/send-otp",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({ email }),
        }
      );

      const msg = await res.text();

      if (res.status === 200) {
        setStep("OTP");
      } else {
        try {
          const data = JSON.parse(msg);
          setError(data.message || msg);
        } catch {
          setError(msg);
        }
      }
    } catch {
      setError("Backend not reachable");
    } finally {
      setLoading(false);
    }
  };

  const verifyOtp = async () => {
    if (!otp) {
      setError("OTP is required");
      return;
    }

    if (otp.length !== 6) {
      setError("OTP must be 6 digits");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const res = await fetch(
        "http://localhost:8080/auth/forgot-password/verify-otp",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({ email, otp }),
        }
      );

      const msg = await res.text();

      if (res.status === 200) {
        navigate("/change-password", { state: { email } });
      } else {
        try {
          const data = JSON.parse(msg);
          setError(data.message || msg);
        } catch {
          setError(msg);
        }
      }
    } catch {
      setError("Backend not reachable");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fp-wrapper">
      <div className="fp-card">
        <h2>Forgot Password</h2>

        {error && <p className="error">{error}</p>}

        {step === "EMAIL" && (
          <>
            <label>Email Address</label>
            <input
              type="email"
              value={email}
              placeholder="Enter your email"
              onChange={(e) => setEmail(e.target.value)}
            />

            <button onClick={sendOtp} disabled={loading}>
              {loading ? "Sending OTP..." : "Send OTP"}
            </button>

            <p className="back" onClick={() => navigate("/login")}>
              ← Back to Login
            </p>
          </>
        )}

        {step === "OTP" && (
          <>
            <label>Enter OTP</label>
            <input
              type="text"
              value={otp}
              maxLength={6}
              placeholder="6-digit OTP"
              onChange={(e) =>
                setOtp(e.target.value.replace(/\D/g, ""))
              }
            />

            <button onClick={verifyOtp} disabled={loading}>
              {loading ? "Verifying..." : "Verify OTP"}
            </button>

            <p className="link" onClick={sendOtp}>
              Resend OTP
            </p>
          </>
        )}
      </div>
    </div>
  );
};

export default ForgotPassword;

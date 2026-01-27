import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/register.css";

const Register = () => {
  const navigate = useNavigate();

  const [step, setStep] = useState("FORM"); // FORM | OTP

  const [form, setForm] = useState({
    name: "",
    email: "",
    prn: "",
    dob: "",
    age: "",
    institute: "",
    phone: "",
  });

  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const calculateAge = (dob) => {
    const birth = new Date(dob);
    const today = new Date();
    let age = today.getFullYear() - birth.getFullYear();
    const m = today.getMonth() - birth.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;
    return age >= 0 ? age : "";
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "dob") {
      setForm({ ...form, dob: value, age: calculateAge(value) });
    } else {
      setForm({ ...form, [name]: value });
    }

    setError("");
  };

  // =========================
  // STEP 1 → SEND OTP
  // =========================
  const sendOtp = async (e) => {
    e.preventDefault();

    if (
      !form.name ||
      !form.email ||
      !form.prn ||
      !form.dob ||
      !form.institute
    ) {
      setError("Please fill all mandatory fields");
      return;
    }

    setLoading(true);

    try {
      const res = await fetch(
        "http://localhost:8080/auth/register/send-otp",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            email: form.email,
            prn: form.prn, // 🔥 PRN MAIL ME JAA RAHA
          }),
        }
      );

      const msg = await res.text();

      if (res.status === 200) {
        setStep("OTP");
      } else {
        setError(msg);
      }
    } catch {
      setError("Backend not reachable");
    } finally {
      setLoading(false);
    }
  };

  // =========================
  // STEP 2 → VERIFY OTP
  // =========================
  const verifyOtp = async () => {
    if (!otp) {
      setError("OTP is required");
      return;
    }

    setLoading(true);

    try {
      const res = await fetch(
        "http://localhost:8080/auth/register/verify-otp",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include", // 🔥 session set hoga
          body: JSON.stringify({
            ...form,
            otp,
          }),
        }
      );

      const msg = await res.text();

      // 🔥 REGISTER → FORCE PASSWORD SET
      if (res.status === 428) {
        navigate("/change-password");
      } else {
        setError(msg);
      }
    } catch {
      setError("Backend not reachable");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">
      <span className="back-login" onClick={() => navigate("/login")}>
        ← Back to Login
      </span>

      <div className="register-card">
        <h2>Student Registration</h2>

        {error && <p className="error">{error}</p>}

        {/* STEP 1 : FORM */}
        {step === "FORM" && (
          <form onSubmit={sendOtp}>
            <label>Full Name *</label>
            <input name="name" onChange={handleChange} />

            <label>Email Address *</label>
            <input name="email" type="email" onChange={handleChange} />

            <label>Roll No / PRN (Username) *</label>
            <input
              name="prn"
              placeholder="e.g. CS23A102"
              onChange={handleChange}
            />

            <label>Phone (optional)</label>
            <input name="phone" onChange={handleChange} />

            <div className="dob-age">
              <div>
                <label>Date of Birth *</label>
                <input name="dob" type="date" onChange={handleChange} />
              </div>
              <div>
                <label>Age</label>
                <input value={form.age} readOnly />
              </div>
            </div>

            <label>School / College *</label>
            <input name="institute" onChange={handleChange} />

            <button type="submit" disabled={loading}>
              {loading ? "Sending OTP..." : "Send Verification OTP"}
            </button>
          </form>
        )}

        {/* STEP 2 : OTP */}
        {step === "OTP" && (
          <>
            <p className="info">
              OTP sent to <b>{form.email}</b>
            </p>

            <label>Enter OTP</label>
            <input
              value={otp}
              maxLength={6}
              onChange={(e) => setOtp(e.target.value)}
            />

            <button onClick={verifyOtp} disabled={loading}>
              {loading ? "Verifying..." : "Verify OTP"}
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default Register;

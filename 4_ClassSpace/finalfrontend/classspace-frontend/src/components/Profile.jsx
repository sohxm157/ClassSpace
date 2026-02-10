import { useEffect, useState } from "react";
import Navbar from "./Navbar";
import Toast from "./Toast";
import "../styles/teacherProfile.css";

function Profile() {
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);

  const [profile, setProfile] = useState(null);
  const [originalProfile, setOriginalProfile] = useState(null);

  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState("success");

  // =========================
  // 🔹 FETCH PROFILE
  // =========================
  useEffect(() => {
    fetch("http://localhost:8080/api/student/profile", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load profile");
        return res.json();
      })
      .then((data) => {
        setProfile(data);
        setOriginalProfile(data);
        console.log("DEBUG: Profile Loaded", data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  // =========================
  // 🔹 INPUT CHANGE
  // =========================
  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "phone") {
      const digitsOnly = value.replace(/\D/g, "");
      if (digitsOnly.length > 10) return;
      setProfile({ ...profile, phone: digitsOnly });
      return;
    }

    setProfile({ ...profile, [name]: value });
  };

  // =========================
  // 🔹 VALIDATIONS (DERIVED)
  // =========================
  const phoneInvalid =
    isEditing &&
    profile?.phone &&
    profile.phone.length !== 10;

  const addressInvalid =
    isEditing &&
    profile?.address &&
    profile.address.trim().length < 5;

  // =========================
  // 🔹 CHECK CHANGES
  // =========================
  const hasChanges = () => {
    if (!profile || !originalProfile) return false;

    return (
      profile.phone !== originalProfile.phone ||
      profile.dob !== originalProfile.dob ||
      profile.address !== originalProfile.address
    );
  };

  // =========================
  // 🔹 SAVE
  // =========================
  const handleSave = async () => {
    if (phoneInvalid || addressInvalid) return; // 🛑 HARD STOP

    try {
      const res = await fetch("http://localhost:8080/api/student/profile", {
        method: "PUT",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: profile.email,
          phone: profile.phone,
          dob: profile.dob,
          address: profile.address,
        }),
      });

      if (!res.ok) throw new Error();

      setOriginalProfile(profile);
      setIsEditing(false);

      // Show success toast
      setToastMessage("Profile updated successfully!");
      setToastType("success");
      setShowToast(true);
    } catch {
      // Show error toast
      setToastMessage("Failed to update profile");
      setToastType("error");
      setShowToast(true);
    }
  };

  // =========================
  // 🔹 LOADING / ERROR
  // =========================
  if (loading) {
    return (
      <div className="tp-loader-wrap">
        <div className="loader"></div>
        <p>Loading your profile...</p>
      </div>
    );
  }

  if (!profile) {
    return (
      <>
        <Navbar />
        <div className="tp-page">
          <div className="tp-error-wrap animated fadeIn">
            <div className="error-icon">❌</div>
            <h3>Oops! Something went wrong</h3>
            <p>We couldn't retrieve your profile details.</p>
            <button className="retry-btn" onClick={() => window.location.reload()}>Retry</button>
          </div>
        </div>
      </>
    );
  }

  // =========================
  // 🔹 UI
  // =========================
  return (
    <>
      <Navbar />
      {showToast && (
        <Toast
          message={toastMessage}
          type={toastType}
          onClose={() => setShowToast(false)}
        />
      )}
      <div className="tp-page">
        <div className="tp-container">
          {/* LEFT SIDE */}
          <div className="tp-left">
            <div className="tp-avatar animated scaleIn">
              {profile.name?.charAt(0)?.toUpperCase() || "S"}
            </div>
            <h3 className="tp-name">{profile.name}</h3>
            <div className="tp-role">Student</div>
            <p className="tp-email">{profile.email}</p>

            {/* Edit Button */}
            {!isEditing && (
              <button className="tp-edit-btn" onClick={() => setIsEditing(true)}>
                ✏️ Edit Profile
              </button>
            )}
          </div>

          {/* RIGHT SIDE */}
          <div className="tp-right animated fadeIn">
            {/* Academic Information */}
            <div className="tp-section">
              <h4>Academic Information</h4>
              <div className="tp-grid">
                <div className="tp-item">
                  <label>PRN</label>
                  <span>{profile.prn}</span>
                </div>
                <div className="tp-item">
                  <label>Branch</label>
                  <span>{profile.branch}</span>
                </div>
                <div className="tp-item">
                  <label>Division</label>
                  <span>{profile.division}</span>
                </div>
                <div className="tp-item">
                  <label>Semester</label>
                  <span>{profile.semester || "Not specified"}</span>
                </div>
              </div>
            </div>

            {/* Personal & Contact Details */}
            <div className="tp-section">
              <h4>Personal & Contact Details</h4>
              <div className="tp-grid">
                <div className="tp-item">
                  <label>Contact Number</label>
                  {isEditing ? (
                    <div className="tp-input-wrapper">
                      <input
                        type="text"
                        name="phone"
                        value={profile.phone || ""}
                        onChange={handleChange}
                        placeholder="10 digit number"
                        className={phoneInvalid ? "invalid" : ""}
                      />
                      {phoneInvalid && (
                        <span className="tp-field-error">Must be 10 digits</span>
                      )}
                    </div>
                  ) : (
                    <span>{profile.phone || "Not provided"}</span>
                  )}
                </div>
                <div className="tp-item">
                  <label>Date of Birth</label>
                  {isEditing ? (
                    <input
                      type="date"
                      name="dob"
                      value={profile.dob || ""}
                      onChange={handleChange}
                    />
                  ) : (
                    <span>{profile.dob || "Not provided"}</span>
                  )}
                </div>
                <div className="tp-item full">
                  <label>Residential Address</label>
                  {isEditing ? (
                    <div className="tp-input-wrapper">
                      <textarea
                        name="address"
                        value={profile.address || ""}
                        onChange={handleChange}
                        rows="3"
                        className={addressInvalid ? "invalid" : ""}
                      />
                      {addressInvalid && (
                        <span className="tp-field-error">At least 5 characters required</span>
                      )}
                    </div>
                  ) : (
                    <span>{profile.address || "No address on file"}</span>
                  )}
                </div>
              </div>
            </div>

            {/* Action Buttons (Edit Mode) */}
            {isEditing && (
              <div className="tp-action-buttons">
                <button
                  className="tp-save-btn"
                  disabled={!hasChanges() || phoneInvalid || addressInvalid}
                  onClick={handleSave}
                >
                  💾 Save Changes
                </button>
                <button
                  className="tp-cancel-btn"
                  onClick={() => {
                    setProfile(originalProfile);
                    setIsEditing(false);
                  }}
                >
                  ✖️ Cancel
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}

export default Profile;

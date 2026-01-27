import { useState } from "react";
import Navbar from "./Navbar";
import "../styles/profile.css";

function Profile() {
  const [isEditing, setIsEditing] = useState(false);
  const [profile, setProfile] = useState({
    name: "ABC XYZ",
    email: "abcxyz@email.com",
    prn: "PRN123456",
    phone: "9876543210",
    branch: "CSE",
    division: "A",
    dob: "2003-01-01"
  });

  const [originalProfile, setOriginalProfile] = useState(profile);

  const handleChange = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value });
  };

  const hasChanges =
    JSON.stringify(profile) !== JSON.stringify(originalProfile);

  const handleSave = () => {
    // 🔗 later: API call here
    setOriginalProfile(profile);
    setIsEditing(false);
  };

  return (
    <>
      <Navbar />

      <div className="profile-container">
        <div className="profile-card">

          {/* EDIT BUTTON */}
          {!isEditing && (
            <button className="edit-btn" onClick={() => setIsEditing(true)}>
              Edit
            </button>
          )}

          {/* LEFT SIDE */}
          <div className="profile-left">
            <img
              src="https://via.placeholder.com/120"
              className="profile-photo"
              alt="profile"
            />
            <h3 className="name">{profile.name}</h3>
            <p className="email">{profile.email}</p>
          </div>

          {/* RIGHT SIDE */}
          <div className="profile-right">

            <div className="form-group">
              <label>PRN</label>
              <input type="text" value={profile.prn} disabled />
            </div>

            <div className="form-group">
              <label>Phone Number</label>
              <input
                type="text"
                name="phone"
                value={profile.phone}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </div>

            <div className="form-group">
              <label>Branch</label>
              <input
                type="text"
                name="branch"
                value={profile.branch}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </div>

            <div className="form-group">
              <label>Division</label>
              <input
                type="text"
                name="division"
                value={profile.division}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </div>

            <div className="form-group">
              <label>Date of Birth</label>
              <input
                type="date"
                name="dob"
                value={profile.dob}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </div>

            {/* SAVE BUTTON */}
            {isEditing && (
              <button
                className="save-btn"
                disabled={!hasChanges}
                onClick={handleSave}
              >
                Save Changes
              </button>
            )}

          </div>
        </div>
      </div>
    </>
  );
}

export default Profile;

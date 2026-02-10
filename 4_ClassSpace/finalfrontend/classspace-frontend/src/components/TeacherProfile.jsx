import React, { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "./Navbar";
import "../styles/teacherProfile.css";

const TeacherProfile = () => {
    const [loading, setLoading] = useState(true);
    const [profile, setProfile] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        axios
            .get("http://localhost:8080/api/teacher/profile", {
                withCredentials: true,
            })
            .then((res) => {
                setProfile(res.data);
            })
            .catch((err) => {
                console.error("Profile load error:", err);
                setError(err.response?.data?.message || "Failed to load profile. Please contact support.");
            })
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return (
            <div className="tp-loader-wrap">
                <div className="loader"></div>
                <p>Loading your academic profile...</p>
            </div>
        );
    }

    if (error || !profile) {
        return (
            <>
                <Navbar />
                <div className="tp-page">
                    <div className="tp-error-wrap animated fadeIn">
                        <div className="error-icon">❌</div>
                        <h3>Oops! Something went wrong</h3>
                        <p>{error || "We couldn't retrieve your profile details."}</p>
                        <button className="retry-btn" onClick={() => window.location.reload()}>Retry</button>
                    </div>
                </div>
            </>
        );
    }

    return (
        <>
            <Navbar />
            <div className="tp-page">
                <div className="tp-container">
                    <div className="tp-left">
                        <div className="tp-avatar animated scaleIn">
                            {profile.name?.charAt(0)?.toUpperCase()}
                        </div>
                        <h3 className="tp-name">{profile.name}</h3>
                        <div className="tp-role">Faculty Member</div>
                        <p className="tp-email">{profile.email}</p>
                    </div>

                    <div className="tp-right animated fadeIn">
                        <div className="tp-section">
                            <h4>Academic Specialization</h4>
                            <div className="tp-grid">
                                <div className="tp-item">
                                    <label>Primary Subject</label>
                                    <span>{profile.subject}</span>
                                </div>
                                <div className="tp-item">
                                    <label>Department</label>
                                    <span>Engineering & Technology</span>
                                </div>
                                <div className="tp-item">
                                    <label>Assigned Classes</label>
                                    <span>{profile.assignedClasses || "General"}</span>
                                </div>
                                <div className="tp-item">
                                    <label>Divisions</label>
                                    <span>{profile.assignedDivisions || "All"}</span>
                                </div>
                            </div>
                        </div>

                        <div className="tp-section">
                            <h4>Personal & Contact Details</h4>
                            <div className="tp-grid">
                                <div className="tp-item">
                                    <label>Contact Number</label>
                                    <span>{profile.phone || "Not provided"}</span>
                                </div>
                                <div className="tp-item">
                                    <label>Date of Birth</label>
                                    <span>{profile.dob || "Not provided"}</span>
                                </div>
                                <div className="tp-item full">
                                    <label>Residential Address</label>
                                    <span>{profile.address || "No address on file"}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default TeacherProfile;

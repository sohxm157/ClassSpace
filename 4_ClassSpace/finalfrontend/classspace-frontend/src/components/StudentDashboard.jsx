import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Navbar from "./Navbar";
import "../styles/dashboard.css";

const days = ["MON", "TUE", "WED", "THU", "FRI", "SAT"];

const StudentDashboard = () => {
  const [timetable, setTimetable] = useState({});
  const [announcements, setAnnouncements] = useState([]);
  const [integrity, setIntegrity] = useState({ percentage: 100, coins: 0 });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch timetable
    axios
      .get("http://localhost:8080/api/student/timetable", {
        withCredentials: true,
      })
      .then((res) => {
        setTimetable(res.data?.timetable || {});
      })
      .catch((err) => {
        console.error("Timetable fetch error:", err);
        setTimetable({});
      })
      .finally(() => setLoading(false));

    // Fetch announcements
    axios
      .get("http://localhost:8080/api/student/announcements", {
        withCredentials: true,
      })
      .then((res) => {
        setAnnouncements(res.data || []);
      })
      .catch((err) => {
        console.error("Announcements fetch error:", err);
      });

    // Fetch Integrity Score (Student Profile)
    axios
      .get("http://localhost:8080/api/student/profile", {
        withCredentials: true,
      })
      .then((res) => {
        setIntegrity({
          percentage: res.data?.integrityPercentage || 100,
          coins: res.data?.coins || 0
        });
      })
      .catch((err) => console.error("Integrity fetch error:", err));
  }, []);

  const timeSlots = [
    ...new Set(
      Object.values(timetable)
        .flat()
        .map((t) => `${t.startTime} - ${t.endTime}`)
    ),
  ];

  const handleSlotClick = async (slot, day) => {
    if (!slot) return;

    // 🔴 Students NEVER have lectureId directly
    // Always resolve lecture first

    const timetableId = slot.timetableId ?? slot.id;

    if (!timetableId) {
      console.warn("No timetableId in slot:", slot);
      alert("Invalid timetable slot");
      return;
    }

    try {
      const res = await axios.post(
        "http://localhost:8080/api/lecture/resolve",
        { timetableId },
        { withCredentials: true }
      );

      const lectureDetails = res.data;
      const lectureId = lectureDetails.lectureId;

      if (!lectureId) {
        console.error("No lectureId returned from resolve:", lectureDetails);
        alert("Lecture could not be resolved");
        return;
      }

      navigate(`/lecture/${lectureId}`, {
        state: {
          subject: lectureDetails.subject || slot.subject,
          timeSlot: `${slot.startTime} - ${slot.endTime}`,
          day,
          lectureDate: lectureDetails.lectureDate,
        },
      });
    } catch (e) {
      console.error("Resolve lecture failed:", e?.response?.data || e.message);
      alert(e?.response?.data?.message || "Failed to open lecture.");
    }
  };


  if (loading) return <div className="loader-container"><div className="loader"></div></div>;

  return (
    <>
      <Navbar />
      <div className="dashboard-page-wrapper">
        <div className="dashboard-main-card">

          {/* Header Section */}
          <div className="dashboard-header">
            <h1>Student Dashboard</h1>
            <p className="subtitle">Your academic overview</p>
          </div>

          {/* INTEGRITY SCORE - HIGHLIGHTED AT TOP */}
          <div className="integrity-highlight-card">
            <div className="score-section">
              <div className="score-ring">
                <span className="sc-value">{integrity.percentage}%</span>
              </div>
              <div className="score-label">
                <h3>Integrity Score</h3>
                <span>Trust Rating</span>
              </div>
            </div>
            <div className="coin-section">
              <span className="coin-icon">🪙</span>
              <div>
                <div className="coin-value">{integrity.coins}</div>
                <div className="coin-label">ClassSpace Coins</div>
              </div>
            </div>
          </div>

          <div className="dashboard-grid-layout">

            {/* LEFT COLUMN: ANNOUNCEMENTS */}
            <div className="dashboard-col-left">
              <div className="section-card announcements-card">
                <div className="card-header">
                  <h2>📢 Announcements</h2>
                </div>
                <div className="card-body-scroll">
                  {announcements.length === 0 ? (
                    <div className="empty-state">No announcements yet.</div>
                  ) : (
                    <div className="announcements-list">
                      {announcements.map((ann) => (
                        <div key={ann.announcementId} className="announcement-item">
                          <div className="announcement-header">
                            <h4>{ann.title}</h4>
                            <span className="announcement-date">
                              {new Date(ann.createdAt).toLocaleDateString()}
                            </span>
                          </div>
                          <p className="announcement-message">{ann.message}</p>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* RIGHT COLUMN: TIMETABLE */}
            <div className="dashboard-col-right">
              <div className="section-card timetable-card">
                <div className="card-header">
                  <h2>📅 Weekly Timetable</h2>
                </div>
                <div className="card-body">
                  <div style={{ overflowX: 'auto' }}>
                    <table className="premium-table">
                      <thead>
                        <tr>
                          <th className="sticky-col">Time</th>
                          {days.map((day) => (
                            <th key={day}>{day}</th>
                          ))}
                        </tr>
                      </thead>

                      <tbody>
                        {timeSlots.map((time) => (
                          <tr key={time}>
                            <td className="time-col sticky-col">{time}</td>

                            {days.map((day) => {
                              const slot =
                                timetable[day]?.find(
                                  (t) => `${t.startTime} - ${t.endTime}` === time
                                ) || null;

                              return (
                                <td
                                  key={day}
                                  className={slot ? "lecture-cell has-slot" : "empty-cell"}
                                  onClick={() => handleSlotClick(slot, day)}
                                >
                                  {slot ? (
                                    <div className="slot-bubble">
                                      <span className="subject">{slot.subject}</span>
                                      <span className="div-tag">{slot.teacherName || slot.teacher}</span>
                                    </div>
                                  ) : (
                                    <span className="dash">—</span>
                                  )}
                                </td>
                              );
                            })}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </>
  );
};

export default StudentDashboard;

import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Navbar from "./Navbar";
import "../styles/dashboard.css";

const days = ["MON", "TUE", "WED", "THU", "FRI", "SAT"];

const TeacherDashboard = () => {
  const [timetable, setTimetable] = useState({});
  const [loading, setLoading] = useState(true);
  const [userName, setUserName] = useState("Professor");
  const navigate = useNavigate();

  // ==========================
  // FETCH USER INFO
  // ==========================
  const fetchUserInfo = () => {
    axios.get("http://localhost:8080/auth/me", { withCredentials: true })
      .then(res => setUserName(res.data.name || "Professor"))
      .catch(() => { });
  };

  // ==========================
  // FETCH TIMETABLE (REUSABLE)
  // ==========================
  const fetchTimetable = () => {
    setLoading(true);

    axios
      .get("http://localhost:8080/api/teacher/timetable", {
        withCredentials: true,
      })
      .then((res) => {
        const rawData = res.data || [];

        const grouped = rawData.reduce((acc, curr) => {
          if (!acc[curr.day]) acc[curr.day] = [];
          acc[curr.day].push(curr);
          return acc;
        }, {});

        setTimetable(grouped);
      })
      .catch((err) => {
        console.error("Timetable fetch error:", err);
        setTimetable({});
      })
      .finally(() => setLoading(false));
  };

  // initial load
  useEffect(() => {
    fetchUserInfo();
    fetchTimetable();
  }, []);

  const timeSlots = timetable
    ? [
      ...new Set(
        Object.values(timetable)
          .flat()
          .map((t) => `${t.startTime} - ${t.endTime}`)
      ),
    ]
    : [];

  // ==========================
  // LECTURE RESOLVE
  // ==========================
  const handleSlotClick = async (slot, day, time) => {
    if (!slot) return;

    const timetableId = slot.timetableId ?? slot.id ?? null;
    if (!timetableId) {
      console.warn("No timetableId in slot:", slot);
      alert("No timetableId found in slot. Backend must send timetableId.");
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

      navigate(`/teacher/lecture/${lectureId}/feedback`, {
        state: {
          subject: lectureDetails.subject || slot.subject,
          timeSlot: time,
          day,
          lectureDate: lectureDetails.lectureDate,
        },
      });

    } catch (e) {
      console.error("Resolve lecture failed:", e?.response?.data || e.message);
      alert(e?.response?.data?.message || "Failed to open lecture.");
    }
  };

  if (loading) {
    return (
      <>
        {/* 🔥 PASS CALLBACK HERE */}
        <Navbar onSlotCreated={fetchTimetable} />

        <div className="dashboard">
          <div className="card timetable">
            <div className="card-body">Loading timetable...</div>
          </div>
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar onSlotCreated={fetchTimetable} />

      <div className="dashboard teacher-dashboard">
        <div className="teacher-polish-wrapper">
          <header className="dashboard-header animated fadeIn">
            <div className="header-content">
              <h1>Welcome, <span className="highlight">{userName}</span></h1>
              <p className="subtitle">Here's your schedule for the upcoming week.</p>
            </div>
          </header>

          <div className="dashboard-grid">
            {/* ANNOUCEMENTS SECTION */}
            <div className="glass-card announcements-card animated slideInUp">
              <div className="card-header">
                <span className="icon">📢</span>
                <h2>Announcements</h2>
              </div>
              <div className="card-body">
                <div className="empty-state">
                  <div className="empty-icon">🔔</div>
                  <p>No new announcements. You're all caught up!</p>
                </div>
              </div>
            </div>

            {/* TIMETABLE SECTION */}
            <div className="glass-card timetable-card animated slideInUp delay-1">
              <div className="card-header">
                <span className="icon">📅</span>
                <h2>Weekly Timetable</h2>
              </div>

              <div className="card-body">
                {timeSlots.length === 0 ? (
                  <div className="empty-table">
                    <div className="empty-icon">🗓️</div>
                    <p>No slots found in your timetable.</p>
                  </div>
                ) : (
                  <div className="table-responsive">
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
                                  (t) =>
                                    `${t.startTime} - ${t.endTime}` === time
                                ) || null;

                              return (
                                <td
                                  key={day}
                                  className={slot ? "lecture-cell has-slot" : "empty-cell"}
                                  onClick={() =>
                                    handleSlotClick(slot, day, time)
                                  }
                                >
                                  {slot ? (
                                    <div className="slot-bubble">
                                      <span className="subject">{slot.subject}</span>
                                      <span className="div-tag">{slot.division}</span>
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
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default TeacherDashboard;

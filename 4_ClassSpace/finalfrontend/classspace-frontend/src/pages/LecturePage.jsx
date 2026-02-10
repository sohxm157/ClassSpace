import { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import axios from "axios";
import Navbar from "../components/Navbar";
import AttendanceSection from "../components/AttendanceSection";
import FeedbackSection from "../components/FeedbackSection";
import "../styles/lecture-page.css";

function LecturePage() {
  const { lectureId } = useParams();
  const location = useLocation();
  
  const [lectureData, setLectureData] = useState({
    subject: location.state?.subject || null,
    timeSlot: location.state?.timeSlot || null,
    date: location.state?.date || location.state?.day || null,
  });

  // Fetch lecture details if not passed via location.state
  useEffect(() => {
    // Use location.state if available
    if (location.state?.subject) {
      setLectureData({
        subject: location.state.subject,
        timeSlot: location.state.timeSlot || "—",
        date: location.state.date || location.state.day || "—",
      });
    } else if (lectureId) {
      // Try to fetch lecture details from backend if available
      axios
        .get(`http://localhost:8080/api/lecture/${lectureId}`, {
          withCredentials: true,
        })
        .then((res) => {
          if (res.data) {
            setLectureData({
              subject: res.data.subject || `Lecture #${lectureId}`,
              timeSlot: res.data.timeSlot || (res.data.startTime && res.data.endTime 
                ? `${res.data.startTime} - ${res.data.endTime}` 
                : "—"),
              date: res.data.date || res.data.day || "—",
            });
          }
        })
        .catch(() => {
          // If fetch fails, use fallback
          setLectureData({
            subject: `Lecture #${lectureId}`,
            timeSlot: "—",
            date: "—",
          });
        });
    }
  }, [lectureId, location.state]);

  const subject = lectureData.subject || `Lecture #${lectureId}`;
  const timeSlot = lectureData.timeSlot || "—";
  const date = lectureData.date || "—";

  return (
    <>
      <Navbar />
      <div className="lecture-page">
        <div className="lecture-card">
          <div className="lecture-card-header">
            <h2 className="lecture-subject">{subject}</h2>
            <div className="lecture-meta">
              {timeSlot} • {date}
            </div>
          </div>

          <div className="lecture-card-body">
            <AttendanceSection lectureId={lectureId} />
            <div className="lp-dash" />
            <FeedbackSection lectureId={lectureId} />
          </div>
        </div>
      </div>
    </>
  );
}

export default LecturePage;
